package com.hxl.desktop.file.service.impl

import com.hxl.desktop.common.bean.FileAttribute
import com.hxl.desktop.common.core.Constant
import com.hxl.desktop.common.core.Directory
import com.hxl.desktop.common.core.NotifyWebSocket
import com.hxl.desktop.file.emun.FileType
import com.hxl.desktop.file.extent.canReadAndWrite
import com.hxl.desktop.file.extent.getAttribute
import com.hxl.desktop.file.extent.getFileSuffixValue
import com.hxl.desktop.file.extent.listRootDirector
import com.hxl.desktop.file.service.IFileService
import com.hxl.desktop.file.utils.ClassPathUtils
import com.hxl.desktop.file.utils.FileCompressUtils
import com.hxl.desktop.file.utils.ImageUtils
import com.hxl.desktop.system.core.AsyncResultWithID
import com.hxl.desktop.system.core.WebSocketMessageBuilder
import com.hxl.desktop.system.core.WebSocketSender
import com.hxl.desktop.system.manager.ClipboardManager
import common.bean.UploadInfo
import common.extent.toFile
import common.extent.toPath
import common.result.FileHandlerResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.ClassPathResource
import org.springframework.scheduling.annotation.AsyncResult
import org.springframework.stereotype.Service
import org.springframework.util.FileSystemUtils
import org.tukaani.xz.CorruptedInputException
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
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
    private val log: Logger = LoggerFactory.getLogger(FileServiceImpl::class.java);
    private val fileMergeLockMap: ConcurrentHashMap<String, Any> = ConcurrentHashMap()

    @Autowired
    lateinit var webSocketSender: WebSocketSender
    override fun fileCut(path: String): Boolean {
        return ClipboardManager.fileCut(path)
    }

    override fun fileCopy(path: String): Boolean {
        return ClipboardManager.fileCopy(path)
    }

    @NotifyWebSocket(subject = "/event/file", action = "paste")
    override fun filePaste(path: String, taskId: String): Future<FileHandlerResult> {
        return AsyncResultWithID(ClipboardManager.filePaste(path), taskId)
    }

    override fun fileRename(source: String, newName: String): FileHandlerResult {
        var sourceFile = source.toFile()
        var target = File(sourceFile.parent, newName)

        if (!sourceFile.exists()) {
            return FileHandlerResult.NOT_EXIST
        }
        if (target.exists()) {
            return FileHandlerResult.TARGET_EXIST
        }
        if (!hasPermission(source)) {
            return FileHandlerResult.NO_PERMISSION
        }
        sourceFile.renameTo(target);
        return FileHandlerResult.OK
    }


    override fun fileMerge(chunkId: String, name: String, inPath: String): FileHandlerResult {
        var rootPath = Paths.get(Directory.getChunkDirectory(), chunkId).toString();
        if (!rootPath.toPath().exists()) {
            return FileHandlerResult.TARGET_NOT_EXIST
        }
        var target = Paths.get(inPath, name);
        //如果目标已经存在
        if (target.exists()) {
            deleteFile(rootPath);
            return FileHandlerResult.TARGET_EXIST
        }
        var fileSize = Files.list(rootPath.toPath()).count()
        var targetOutputStream = target.outputStream()
        for (i in 0 until fileSize) {
            targetOutputStream.write(Files.readAllBytes(Paths.get(rootPath, i.toString())))
        }
        targetOutputStream.flush()
        targetOutputStream.close()
        deleteFile(rootPath);
        //通知客户端
        webSocketSender.send(
            WebSocketMessageBuilder.Builder()
                .applySubject(Constant.WebSocketSubjectNameConstant.REFRESH_FOLDER)
                .applyAction("refresh")
                .addItem("inPath", inPath)
                .build()
        )
        return FileHandlerResult.OK;
    }

    override fun chunkUpload(uploadInfo: UploadInfo): FileHandlerResult {
        //上传的时候检测目标是否否存在，这要有一个chunk失败了，前端会提示，并且取消其他请求
        try {
            var target = Paths.get(uploadInfo.target, uploadInfo.fileName);
            if (target.exists()) {
                log.info("目标{}已存在", target)
                return FileHandlerResult.TARGET_EXIST
            }
            if (!uploadInfo.target.toFile().canReadAndWrite()) {
                log.info("目标{}无操作全想", uploadInfo.target)
                return FileHandlerResult.NO_PERMISSION
            }
            var chunkLock = fileMergeLockMap.getOrPut(uploadInfo.chunkId) { Any() }

            var chunkDirector = Paths.get(Directory.createChunkDirector(uploadInfo.chunkId));
            Files.write(
                Paths.get(chunkDirector.toString(), uploadInfo.blobId.toString()),
                uploadInfo.fileBinary.inputStream.readBytes()
            );
            var currentSize = Files.list(chunkDirector).map { it.fileSize() }.collect(Collectors.toList()).sum();
            //如果文件大小等于当前文件数量合，和并文件
            //上传完毕后只有一个线程可以进行和并
            synchronized(chunkLock) {
                //如果没有找到key，则说明其他线程已经合并了
                if (!fileMergeLockMap.containsKey(uploadInfo.chunkId)) {
                    return FileHandlerResult.OK;
                }
                //判断全部上传成功没，如果成功，则进行合并
                if (uploadInfo.total == currentSize) {
                    var mergeResult = fileMerge(uploadInfo.chunkId, uploadInfo.fileName, uploadInfo.target)
                    fileMergeLockMap.remove(uploadInfo.chunkId)
                    return mergeResult
                }
            }
            return FileHandlerResult.OK;
        } catch (e: Exception) {
            log.info("上传失败{}", e.message)
        }
        return FileHandlerResult.UPLOAD_FAIL
    }

    override fun listDirector(root: String): List<FileAttribute> {
        if (!root.toFile().canRead()) {
            return emptyList()
        }

        var files = root.toPath().listRootDirector()
        var mutableListOf = mutableListOf<FileAttribute>()
        files.forEach { mutableListOf.add(it.getAttribute()) }
        var folderList = mutableListOf.filter { it.type == FileType.FOLDER.typeName }
        var fileList = mutableListOf.filter { it.type != FileType.FOLDER.typeName }
        folderList.sortedBy { it.name }
        fileList.sortedBy { it.name }
        return folderList.plus(fileList)
    }

    override fun getImageThumbnail(path: String): ByteArrayResource {
        if (path.toFile().exists()) {
            if (path.toPath().isDirectory()) {
                var classPathResource = ClassPathResource(ClassPathUtils.getClassPathFullPath("folder"))
                return ByteArrayResource(classPathResource.inputStream.readBytes())
            }
            var fileAttribute = path.toFile().getAttribute()
            if ("image" == fileAttribute.type) {
                var byteArrayResource = ImageUtils.thumbnails(path)
                if (byteArrayResource != null) {
                    return byteArrayResource
                }
                return getFileIconByType(fileAttribute.rawType)
            }
        }
        var defaultIcon = ClassPathResource(ClassPathUtils.getClassPathFullPath("file"))
        return ByteArrayResource(defaultIcon.inputStream.readBytes())
    }

    override fun getFileIconByType(type: String): ByteArrayResource {
        var classPathResource = ClassPathResource(ClassPathUtils.getClassPathFullPath(type))
        if (classPathResource.exists()) {
            return ByteArrayResource(classPathResource.inputStream.readBytes())
        }
        var defaultIcon = ClassPathResource(ClassPathUtils.getClassPathFullPath("file"))
        return ByteArrayResource(defaultIcon.inputStream.readBytes())
    }

    override fun hasPermission(path: String): Boolean {
        Paths.get(path).getOwner()?.let {
            return (System.getProperty("user.name") == it.name)
        }
        return false;
    }

    override fun deleteFile(path: String): FileHandlerResult {
        if (hasPermission(path)) {
            FileSystemUtils.deleteRecursively(Paths.get(path))
            return FileHandlerResult.OK
        }
        return FileHandlerResult.NO_PERMISSION
    }

    @NotifyWebSocket(subject = Constant.WebSocketSubjectNameConstant.COMPRESS_RESULT, action = "")
    override fun fileCompress(
        path: String,
        targetName: String,
        compressType: String,
        taskId: String
    ): Future<FileHandlerResult> {
        var archiveName = "$targetName.$compressType"
        var file = path.toFile()
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

    @NotifyWebSocket(subject = Constant.WebSocketSubjectNameConstant.COMPRESS_RESULT, action = "")
    override fun fileDecompression(path: String, taskId: String): Future<FileHandlerResult> {
        var file = path.toFile()
        if(file.isDirectory){
            return AsyncResultWithID(FileHandlerResult.IS_DIRECTORY, taskId)
        }
        if (!file.exists()) {
            return AsyncResultWithID(FileHandlerResult.TARGET_EXIST, taskId)
        }
        var fileType = FileCompressUtils.getFileType(path)
        if (fileType.isNotEmpty()) {
            try {
                FileCompressUtils.getCompressByType(fileType)?.run {
                    this.decompression(path)
                }
            } catch (e: Exception) {
                if (e is CorruptedInputException) {
                    println(e.message)
                }
            }
            return AsyncResultWithID(FileHandlerResult.OK, taskId)
        }
        return AsyncResultWithID(FileHandlerResult.OK, taskId)
    }

    override fun createFile(parent: String, name: String, type: String): FileHandlerResult {
        try {
            if ("director" == type) {
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
        var file = path.toFile()
        if (file.exists()) {
            var infoMap = mutableMapOf<String, Any>()
            infoMap["content"] = file.bufferedReader().readText()
            infoMap["type"] = file.getFileSuffixValue()
            return FileHandlerResult.create(0, infoMap, "ok")
        }
        return FileHandlerResult.NOT_EXIST
    }

    override fun setTextFileContent(path: String, content: String): FileHandlerResult {
        var file = path.toFile()
        if (!file.exists())
            return FileHandlerResult.NOT_EXIST
        if (hasPermission(file.parent)) {
            Files.write(Paths.get(path), content.toByteArray())
            return FileHandlerResult.OK
        }
        return FileHandlerResult.NO_PERMISSION
    }
}