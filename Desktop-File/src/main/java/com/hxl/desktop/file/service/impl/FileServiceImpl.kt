package com.hxl.desktop.file.service.impl

import com.hxl.desktop.common.result.FileHandlerResult
import com.hxl.desktop.common.bean.FileAttribute
import com.hxl.desktop.common.bean.UploadInfo
import com.hxl.desktop.common.extent.toPath
import com.hxl.desktop.common.manager.ClipboardManager
import com.hxl.desktop.file.compress.FileCompress
import com.hxl.desktop.file.emun.FileType
import com.hxl.desktop.file.extent.toFileAttribute
import com.hxl.desktop.file.service.IFileService
import com.hxl.desktop.file.utils.Directory
import com.hxl.desktop.file.utils.FileTypeRegister
import net.coobird.thumbnailator.Thumbnails
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.ClassPathResource
import org.springframework.scheduling.annotation.AsyncResult
import org.springframework.stereotype.Service
import org.springframework.util.FileSystemUtils
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.Future
import kotlin.io.path.*
import kotlin.streams.toList

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
        ClipboardManager.fileCut(path);
        return true
    }

    override fun fileRename(source: String, newName: String): FileHandlerResult {
        var file = File(source)
        var target = File(file.parent, newName)
        if (!file.exists()) {
            return FileHandlerResult.EXIST
        }
        if (target.exists()) {
            return FileHandlerResult.TARGET_EXIST
        }
        if (!hasPermission(source)) {
            return FileHandlerResult.NO_PERMISSION
        }
        log.info("file rename source=>{} target=>{}", source, newName)
        file.renameTo(target);
        return FileHandlerResult.OK;
    }

    override fun fileCopy(path: String): Boolean {
        if (path.toPath().exists()) {
            ClipboardManager.fileCopy(path)
            return true;
        }
        return false;
    }

    override fun filePaste(path: String): FileHandlerResult {
        if (path.toPath().exists()) {
            return ClipboardManager.filePaste(path)

        }
        return FileHandlerResult.EXIST;
    }

    override fun fileMerge(chunkId: String, name: String, inPath: String): FileHandlerResult {
        var rootPath = Paths.get(Directory.getChunkDirectory(), chunkId).toString();
        var target = Paths.get(inPath, name);
        log.info("fileMerge->{}", chunkId)
        if (target.exists()) {
            return FileHandlerResult.EXIST
        }
        var size = Files.list(Paths.get(rootPath)).count()
        var targetOutputStream = target.outputStream()
        for (i in 0 until size) {
            targetOutputStream.write(Files.readAllBytes(Paths.get(rootPath, i.toString())))
        }
        targetOutputStream.flush()
        targetOutputStream.close()
        deleteFile(rootPath);
        return FileHandlerResult.OK;
    }

    override fun checkUploadFile(uploadInfo: UploadInfo): Boolean {
        log.info("file upload  {}", uploadInfo.fileName)
        var chunkDirector = Paths.get(Directory.createChunkDirector(uploadInfo.chunkId));
        Files.write(Paths.get(chunkDirector.toString(), uploadInfo.blobId.toString()), uploadInfo.fileBinary.inputStream.readBytes());
        var currentSize = Files.list(chunkDirector).map { it.fileSize() }.toList().sum();
        log.info("upload finish current size={},target={}", currentSize, uploadInfo.total)
        if (uploadInfo.total == currentSize) {
            fileMerge(uploadInfo.chunkId, uploadInfo.fileName, uploadInfo.target)
        }
        return true;
    }

    override fun listDirector(root: String): List<FileAttribute> {
        var listDirector = Directory.listDirector(root)
        var mutableListOf = mutableListOf<FileAttribute>()
        for (file in listDirector) {
            file.toFileAttribute()?.let { mutableListOf.add(it) }
        }
        var folderList = mutableListOf.filter { it.type == FileType.FOLDER.typeName }
        var fileList = mutableListOf.filter { it.type != FileType.FOLDER.typeName }
        folderList.sortedBy { it.name }
        fileList.sortedBy { it.name }
        return folderList.plus(fileList)
    }

    override fun getImageThumbnail(path: String): ByteArrayResource {
        if (Paths.get(path).isDirectory()) {
            var classPathResource = ClassPathResource(FileTypeRegister.getFullPath("folder"))
            return ByteArrayResource(classPathResource.inputStream.readBytes())
        }
        var fileAttribute = Paths.get(path).toFileAttribute()
        fileAttribute?.let {
            if (fileAttribute.type == "img") {
                var bufferedOutputStream = ByteArrayOutputStream()
                try {
                    Thumbnails.of(path)
                            .outputQuality(0.3)
                            .scale(0.3)
                            .toOutputStream(bufferedOutputStream)
                    return ByteArrayResource(bufferedOutputStream.toByteArray())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        var defaultIcon = ClassPathResource(FileTypeRegister.getFullPath("file"))
        return ByteArrayResource(defaultIcon.inputStream.readBytes())
    }

    override fun getFileIconByType(type: String): ByteArrayResource {
        var classPathResource = ClassPathResource(FileTypeRegister.getFullPath(type))
        if (classPathResource.exists()) {
            return ByteArrayResource(classPathResource.inputStream.readBytes())
        }
        var defaultIcon = ClassPathResource(FileTypeRegister.getFullPath("file"))
        return ByteArrayResource(defaultIcon.inputStream.readBytes())
    }

    override fun hasPermission(path: String): Boolean {
        var path = Paths.get(path)
        path.getOwner()?.let {
            return (System.getProperty("user.name") == it.name)
        }
        return false;

    }

    override fun deleteFile(path: String): FileHandlerResult {
        if (hasPermission(path)) {
            FileSystemUtils.deleteRecursively(Paths.get(path))
            log.info("delete file->{}", path)
            return FileHandlerResult.OK
        }
        return FileHandlerResult.NO_PERMISSION
    }

    override fun fileCompress(path: String, targetName: String, compressType: String): Future<FileHandlerResult> {
         FileCompress.getCompressByType(compressType).compress(path,targetName)
        return AsyncResult(FileHandlerResult.OK)
    }
}