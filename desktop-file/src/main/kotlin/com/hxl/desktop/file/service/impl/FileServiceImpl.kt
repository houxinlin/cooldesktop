package com.hxl.desktop.file.service.impl

import com.hxl.desktop.common.core.Constant
import com.hxl.desktop.common.core.Directory
import com.hxl.desktop.system.ano.NotifyWebSocket
import com.hxl.desktop.common.extent.toFile
import com.hxl.desktop.common.extent.toPath
import com.hxl.desktop.common.result.FileHandlerResult
import com.hxl.desktop.database.CoolDesktopDatabase
import com.hxl.desktop.file.bean.FileAttribute
import com.hxl.desktop.file.bean.UploadInfo
import com.hxl.desktop.file.emun.FileType
import com.hxl.desktop.file.extent.*
import com.hxl.desktop.file.service.IFileService
import com.hxl.desktop.file.utils.ClassPathUtils
import com.hxl.desktop.file.utils.FileCompressUtils
import com.hxl.desktop.file.utils.ImageUtils
import com.hxl.desktop.system.core.AsyncResultWithID
import com.hxl.desktop.system.core.WebSocketMessageBuilder
import com.hxl.desktop.system.core.WebSocketSender
import com.hxl.desktop.system.manager.ClipboardManager
import com.hxl.desktop.system.tail.TailManager
import com.hxl.desktop.system.terminal.LinuxShell
import com.hxl.desktop.system.utils.JarUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.FileSystemResource
import org.springframework.http.ResponseEntity
import org.springframework.scheduling.annotation.AsyncResult
import org.springframework.stereotype.Service
import org.springframework.util.FileSystemUtils
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Future
import java.util.stream.Collectors
import kotlin.io.path.*

/**
 * @author:   HouXinLin
 * @email:    2606710413@qq.com
 * @date:     20212021/12/18
 * @describe:
 * @version:  v1.0
 */
@Service
class FileServiceImpl : IFileService {
    private val log: Logger = LoggerFactory.getLogger(FileServiceImpl::class.java)
    private val fileMergeLockMap: ConcurrentHashMap<String, Any> = ConcurrentHashMap()

    @Autowired
    lateinit var webSocketSender: WebSocketSender

    @Autowired
    lateinit var coolDesktopDatabase: CoolDesktopDatabase

    override fun fileCut(path: String): Boolean {
        return ClipboardManager.fileCut(path)
    }

    override fun fileCopy(path: String): Boolean {
        return ClipboardManager.fileCopy(path)
    }

    /**
     * ??????????????????????????????????????????????????????WebSocket???????????????
     */
    @NotifyWebSocket(subject = Constant.WebSocketSubjectNameConstant.FILE_EVENT, action = "paste")
    override fun filePaste(path: String, taskId: String): Future<FileHandlerResult> {
        return AsyncResultWithID(ClipboardManager.filePaste(path), taskId)
    }

    /**
     * ???????????????
     */
    override fun fileRename(source: String, newName: String): FileHandlerResult {
        val sourceFile = source.toFile()
        val target = File(sourceFile.parent, newName)

        if (!sourceFile.exists()) {
            return FileHandlerResult.NOT_EXIST
        }
        if (target.exists()) {
            return FileHandlerResult.TARGET_EXIST
        }
        if (!hasPermission(source)) {
            return FileHandlerResult.NO_PERMISSION
        }
        sourceFile.renameTo(target)
        return FileHandlerResult.OK
    }


    /**
     * ????????????
     */
    override fun fileMerge(chunkId: String, name: String, inPath: String): FileHandlerResult {
        val rootPath = Paths.get(Directory.getChunkDirectory(), chunkId).toString()
        if (!rootPath.toPath().exists()) {
            return FileHandlerResult.TARGET_NOT_EXIST
        }
        val target = Paths.get(inPath, name)
        //????????????????????????
        if (target.exists()) {
            deleteFile(rootPath)
            return FileHandlerResult.TARGET_EXIST
        }
        val fileSize = Files.list(rootPath.toPath()).count()
        val targetOutputStream = target.outputStream()
        for (i in 0 until fileSize) {
            targetOutputStream.write(Files.readAllBytes(Paths.get(rootPath, i.toString())))
        }
        targetOutputStream.flush()
        targetOutputStream.close()
        deleteFile(rootPath)
        //???????????????
        webSocketSender.send(
            WebSocketMessageBuilder.Builder()
                .applySubject(Constant.WebSocketSubjectNameConstant.FILE_EVENT)
                .applyAction("refresh")
                .addItem("inPath", inPath)
                .build()
        )
        return FileHandlerResult.OK
    }

    override fun chunkUpload(uploadInfo: UploadInfo): FileHandlerResult {
        //????????????????????????????????????????????????????????????chunk??????????????????????????????????????????????????????
        try {
            val target = Paths.get(uploadInfo.target, uploadInfo.fileName)
            if (target.exists()) {
                log.info("??????{}?????????", target)
                return FileHandlerResult.TARGET_EXIST
            }
            if (!uploadInfo.target.toFile().canReadAndWrite()) {
                log.info("??????{}???????????????", uploadInfo.target)
                return FileHandlerResult.NO_PERMISSION
            }
            val chunkLock = fileMergeLockMap.getOrPut(uploadInfo.chunkId) { Any() }

            val chunkDirector = Paths.get(Directory.createChunkDirector(uploadInfo.chunkId))
            Files.write(
                Paths.get(chunkDirector.toString(), uploadInfo.blobId.toString()),
                uploadInfo.fileBinary.inputStream.readBytes()
            )
            val currentSize = Files.list(chunkDirector).map { it.fileSize() }.collect(Collectors.toList()).sum()
            //????????????????????????????????????????????????????????????
            //???????????????????????????????????????????????????
            synchronized(chunkLock) {
                //??????????????????key???????????????????????????????????????
                if (!fileMergeLockMap.containsKey(uploadInfo.chunkId)) {
                    return FileHandlerResult.OK
                }
                //????????????????????????????????????????????????????????????
                if (uploadInfo.total == currentSize) {
                    val mergeResult = fileMerge(uploadInfo.chunkId, uploadInfo.fileName, uploadInfo.target)
                    fileMergeLockMap.remove(uploadInfo.chunkId)
                    return mergeResult
                }
            }
            return FileHandlerResult.OK
        } catch (e: Exception) {
            log.info("????????????{}", e.message)
        }
        return FileHandlerResult.UPLOAD_FAIL
    }

    /**
     * list??????
     */
    override fun listDirector(root: String): List<FileAttribute> {
        if (!root.toFile().exists()) {
            log.info("?????????????????????????????????{}", root)
            webSocketSender.send(
                WebSocketMessageBuilder.Builder()
                    .applySubject(Constant.WebSocketSubjectNameConstant.NOTIFY_MESSAGE_ERROR)
                    .addItem("data", Constant.StringConstant.FILE_NOT_EXIST)
                    .build()
            )
            return emptyList()
        }
        if (!root.toFile().canRead()) {
            log.info("???????????????{}", root)
            webSocketSender.send(
                WebSocketMessageBuilder.Builder()
                    .applySubject(Constant.WebSocketSubjectNameConstant.NOTIFY_MESSAGE_ERROR)
                    .addItem("data", Constant.StringConstant.NO_PERMISSION)
                    .build()
            )
            return emptyList()
        }
        val files = root.toPath().listRootDirector()
        val mutableListOf = mutableListOf<FileAttribute>()
        files.forEach { mutableListOf.add(it.getAttribute()) }
        val folderList = mutableListOf.filter { it.type == FileType.FOLDER.typeName }
        val fileList = mutableListOf.filter { it.type != FileType.FOLDER.typeName }
        folderList.sortedBy { it.name }
        fileList.sortedBy { it.name }
        return folderList.plus(fileList)
    }

    /**
     * ???????????????
     */
    override fun getImageThumbnail(path: String): ByteArrayResource {
        if (path.toFile().exists()) {
            if (path.toPath().isDirectory()) {
                val classPathResource = ClassPathResource(ClassPathUtils.getClassPathFullPath("folder"))
                return ByteArrayResource(classPathResource.inputStream.readBytes())
            }
            val fileAttribute = path.toFile().getAttribute()
            if ("image" == fileAttribute.type) {
                val byteArrayResource = ImageUtils.thumbnails(path)
                if (byteArrayResource != null) {
                    return byteArrayResource
                }
                return getFileIconByType(fileAttribute.rawType)
            }
        }
        val defaultIcon = ClassPathResource(ClassPathUtils.getClassPathFullPath("file"))
        return ByteArrayResource(defaultIcon.inputStream.readBytes())
    }

    /**
     * ??????????????????????????????icon
     */
    override fun getFileIconByPath(path: String): ByteArrayResource {
        val file = path.toFile()
        if (!file.exists()) {
            val defaultIcon = ClassPathResource(ClassPathUtils.getClassPathFullPath("file"))
            return ByteArrayResource(defaultIcon.inputStream.readBytes())
        }
        val attribute = file.getAttribute()
        return getFileIconByType(attribute.rawType)
    }

    /**
     * ????????????????????????icon
     */
    override fun getFileIconByType(type: String): ByteArrayResource {
        val classPathResource = ClassPathResource(ClassPathUtils.getClassPathFullPath(type))
        if (classPathResource.exists()) {
            return ByteArrayResource(classPathResource.inputStream.readBytes())
        }
        val defaultIcon = ClassPathResource(ClassPathUtils.getClassPathFullPath("file"))
        return ByteArrayResource(defaultIcon.inputStream.readBytes())
    }

    override fun hasPermission(path: String): Boolean {
        return path.toFile().canReadAndWrite()
    }

    override fun deleteFile(path: String): FileHandlerResult {
        if (hasPermission(path)) {
            FileSystemUtils.deleteRecursively(Paths.get(path))
            return FileHandlerResult.OK
        }
        return FileHandlerResult.NO_PERMISSION
    }

    @NotifyWebSocket(subject = Constant.WebSocketSubjectNameConstant.FILE_EVENT, action = "")
    override fun fileCompress(
        path: String,
        targetName: String,
        compressType: String,
        taskId: String
    ): Future<FileHandlerResult> {
        val archiveName = "$targetName.$compressType"
        val file = path.toFile()
        if (file.isFile && Paths.get(file.parent, archiveName).exists()) {
            return AsyncResultWithID(FileHandlerResult.TARGET_EXIST, taskId)
        }
        if (Paths.get(path, archiveName).exists()) {
            return AsyncResultWithID(FileHandlerResult.TARGET_EXIST, taskId)
        }
        FileCompressUtils.getCompressByType(compressType)?.run {
            this.compress(path, "$targetName.$compressType")
            return AsyncResultWithID(FileHandlerResult.OK, taskId)
        }
        return AsyncResultWithID(FileHandlerResult.COMPRESS_FAIL, taskId)
    }

    @NotifyWebSocket(subject = Constant.WebSocketSubjectNameConstant.FILE_EVENT, action = "")
    override fun fileDecompression(path: String, taskId: String): Future<FileHandlerResult> {
        val file = path.toFile()
        if (file.isDirectory) {
            return AsyncResultWithID(FileHandlerResult.IS_DIRECTORY, taskId)
        }
        if (!file.exists()) {
            return AsyncResultWithID(FileHandlerResult.TARGET_EXIST, taskId)
        }
        val fileType = FileCompressUtils.getFileType(path)
        if (fileType.isNotEmpty()) {
            try {
                val compressByType = FileCompressUtils.getCompressByType(fileType)
                compressByType?.decompression(path)
                return AsyncResultWithID(FileHandlerResult.OK, taskId)
            } catch (e: Exception) {
                val msg = e.message as String
                return AsyncResultWithID(FileHandlerResult.fail(msg, msg), taskId)
            }
        }
        return AsyncResultWithID(FileHandlerResult.NOT_SUPPORT_COMPRESS_TYPE, taskId)
    }

    override fun createFile(parent: String, name: String, type: String): FileHandlerResult {
        try {
            if ("folder" == type) {
                Paths.get(parent, name).createDirectories()
            }
            if ("file" == type) {
                Paths.get(parent, name).createFile()
            }
        } catch (e: Exception) {
            return FileHandlerResult.CREATE_FILE_FAIL
        }
        return FileHandlerResult.OK
    }

    override fun getTextFileContent(path: String): FileHandlerResult {
        val file = path.toFile()
        if (file.exists()) {
            val infoMap = mutableMapOf<String, Any>()
            infoMap["content"] = file.bufferedReader().readText()
            infoMap["type"] = file.getFileSuffixValue()
            return FileHandlerResult.create(0, infoMap, "ok")
        }
        return FileHandlerResult.NOT_EXIST
    }

    override fun setTextFileContent(path: String, content: String): FileHandlerResult {
        val file = path.toFile()
        if (!file.exists())
            return FileHandlerResult.NOT_EXIST
        if (hasPermission(file.parent)) {
            Files.write(Paths.get(path), content.toByteArray())
            return FileHandlerResult.OK
        }
        return FileHandlerResult.NO_PERMISSION
    }

    override fun download(path: String): ResponseEntity<FileSystemResource> {
        val downloadPath = path.toPath()
        if (downloadPath.isDirectory() || (!downloadPath.exists())) {
            return ResponseEntity.notFound().build()
        }
        return downloadPath.toFile().toHttpResponse()
    }

    /**
     * ??????jar??????,
     * type=0???????????????
     * type=1,??????
     */
    override fun runJarFile(path: String, arg: String, type: Int): Boolean {
        if ((!path.toFile().exists()) || path.toFile().isDirectory) return false
        val maxWaitSecond = 5L //??????????????????
        if (type == 1) {
            val stopMessage = stopJar(path)
            if (stopMessage != Constant.StringConstant.STOP_JAR_PROCESS_SUCCESS) {
                return false
            }
        }
        JarUtils.getProcessIds(path).let {
            //?????????????????????????????????????????????????????????,?????????????????????????????????????????????????????????????????????
            JarUtils.run(path, arg)
            return JarUtils.isRun(path, it, maxWaitSecond)
        }
    }

    override fun stopJar(path: String): String {
        if (!path.toFile().exists()) return Constant.StringConstant.FILE_NOT_EXIST
        if (JarUtils.getProcessIds(path).isEmpty()) return Constant.StringConstant.STOP_JAR_PROCESS_FAIL_EMPTY
        if (JarUtils.getProcessIds(path).size > 1) return Constant.StringConstant.STOP_JAR_PROCESS_FAIL_MULTI
        JarUtils.stopJar(path)
        return Constant.StringConstant.STOP_JAR_PROCESS_SUCCESS
    }

    /**
     * ?????????????????????websocket??????????????????
     */
    @NotifyWebSocket(subject = Constant.WebSocketSubjectNameConstant.NOTIFY_MESSAGE_SUCCESS, action = "show")
    override fun runShell(path: String): Future<String> {
        return AsyncResult(Constant.StringConstant.SHELL_EXEC_RESULT + LinuxShell(path).run())
    }

    override fun tailStart(path: String): FileHandlerResult {
        if (!path.toFile().exists()) return FileHandlerResult.NOT_EXIST
        return FileHandlerResult.createOK(TailManager.create(path) {
            webSocketSender.send(
                WebSocketMessageBuilder.Builder()
                    .applySubject("/event/tail/$path")
                    .addItem("data", it)
                    .build()
            )
        })
    }

    override fun tailStop(uuid: String): FileHandlerResult {
        TailManager.stop(uuid)
        return FileHandlerResult.OK
    }
}