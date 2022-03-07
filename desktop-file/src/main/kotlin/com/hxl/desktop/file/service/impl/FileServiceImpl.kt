package com.hxl.desktop.file.service.impl

import com.hxl.desktop.common.core.Directory
import com.hxl.desktop.common.core.NotifyWebSocket
import common.result.FileHandlerResult
import common.bean.FileAttribute
import common.bean.UploadInfo
import common.extent.toFile
import common.extent.toPath
import com.hxl.desktop.file.utils.FileCompressUtils
import com.hxl.desktop.file.emun.FileType
import com.hxl.desktop.file.extent.*
import com.hxl.desktop.file.service.IFileService
import com.hxl.desktop.file.utils.ClassPathUtils
import com.hxl.desktop.file.utils.ImageUtils
import com.hxl.desktop.system.core.AsyncResultWithID
import common.manager.ClipboardManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.ClassPathResource
import org.springframework.scheduling.annotation.AsyncResult
import org.springframework.stereotype.Service
import org.springframework.util.FileSystemUtils
import org.tukaani.xz.CorruptedInputException
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.Future
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
    var log: Logger = LoggerFactory.getLogger(FileServiceImpl::class.java);

    override fun fileCut(path: String): Boolean {
        return ClipboardManager.fileCut(path)
    }

    override fun fileCopy(path: String): Boolean {
        return ClipboardManager.fileCopy(path)
    }

    @NotifyWebSocket(subject = "/file/events", action = "paste")
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
        var target = Paths.get(inPath, name);
        if (target.exists()) {
            return FileHandlerResult.EXIST
        }
        var size = Files.list(rootPath.toPath()).count()
        var targetOutputStream = target.outputStream()
        for (i in 0 until size) {
            targetOutputStream.write(Files.readAllBytes(Paths.get(rootPath, i.toString())))
        }
        targetOutputStream.flush()
        targetOutputStream.close()
        deleteFile(rootPath);
        return FileHandlerResult.OK;
    }

    override fun chunkUpload(uploadInfo: UploadInfo): Boolean {
        var chunkDirector = Paths.get(Directory.createChunkDirector(uploadInfo.chunkId));
        Files.write(
            Paths.get(chunkDirector.toString(), uploadInfo.blobId.toString()),
            uploadInfo.fileBinary.inputStream.readBytes()
        );
        var currentSize = Files.list(chunkDirector).map { it.fileSize() }.toList().sum();
        if (uploadInfo.total == currentSize) {
            fileMerge(uploadInfo.chunkId, uploadInfo.fileName, uploadInfo.target)
        }
        return true;
    }

    override fun listDirector(root: String): List<FileAttribute> {
        if (!root.toFile().canRead()) { return emptyList() }
        var files = root.toPath().listRootDirector()
        var mutableListOf = mutableListOf<FileAttribute>()
        files.forEach { mutableListOf.add(it.toFile().getAttribute()) }
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

    override fun fileCompress(path: String, targetName: String, compressType: String): Future<FileHandlerResult> {
        var archiveName = "$targetName.$compressType"
        var file = path.toFile()
        if (file.isFile && Paths.get(file.parent, archiveName).exists()) {
            return AsyncResult(FileHandlerResult.TARGET_EXIST)
        }
        if (Paths.get(path, archiveName).exists()) {
            return AsyncResult(FileHandlerResult.TARGET_EXIST)
        }
        FileCompressUtils.getCompressByType(compressType).compress(path, "$targetName.$compressType")
        return AsyncResult(FileHandlerResult.OK)
    }

    override fun fileDecompression(path: String): Future<FileHandlerResult> {
        var file = path.toFile()
        if (!file.exists()) {
            return AsyncResult(FileHandlerResult.TARGET_EXIST)
        }
        var fileType = FileCompressUtils.getFileType(path)
        if (fileType.isNotEmpty()) {
            try {
                FileCompressUtils.getCompressByType(fileType).decompression(path)
            } catch (e: Exception) {
                if (e is CorruptedInputException) {
                    println(e.message)
                }
            }
            return AsyncResult(FileHandlerResult.OK)
        }
        return AsyncResult(FileHandlerResult.OK)
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