package com.hxl.desktop.file.service.impl

import com.hxl.desktop.common.result.FileHandlerResult
import com.hxl.desktop.common.bean.FileAttribute
import com.hxl.desktop.common.bean.UploadInfo
import com.hxl.desktop.file.extent.toFileAttribute
import com.hxl.desktop.file.service.IFileService
import com.hxl.desktop.file.utils.Directory
import com.hxl.desktop.file.utils.FileTypeRegister
import com.hxl.desktop.file.utils.ZipUtils
import net.coobird.thumbnailator.Thumbnails
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import org.springframework.util.FileSystemUtils
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayOutputStream
import java.nio.file.Files
import java.nio.file.Paths
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
        return mutableListOf;
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
                            .outputQuality(0.5)
                            .scale(0.5)
                            .toOutputStream(bufferedOutputStream)
                    return ByteArrayResource(bufferedOutputStream.toByteArray())
                } catch (v: Exception) {

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

    override fun deleteFile(path: String): FileHandlerResult {
        var path = Paths.get(path)
        path.getOwner()?.let {
            if (System.getProperty("user.name") == it.name) {
                FileSystemUtils.deleteRecursively(path)
                log.info("delete file->{}", path)
                return FileHandlerResult.OK
            }
            return FileHandlerResult.NO_PERMISSION
        }
        return FileHandlerResult.NONE;
    }
}