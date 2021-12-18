package com.hxl.desktop.file.service.impl

import com.hxl.desktop.file.bean.FileAttribute
import com.hxl.desktop.file.extent.toFileAttribute
import com.hxl.desktop.file.service.IFileService
import com.hxl.desktop.file.utils.Directory
import com.hxl.desktop.file.utils.FileIconRegister
import net.coobird.thumbnailator.Thumbnailator
import net.coobird.thumbnailator.Thumbnails
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import org.springframework.util.ResourceUtils
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.isDirectory

/**
 * @author:   HouXinLin
 * @email:    2606710413@qq.com
 * @date:     20212021/12/18
 * @describe:
 * @version:  v1.0
 */
@Service
class FileServiceImpl : IFileService {
    override fun listDirector(root: String): List<FileAttribute> {
        var listDirector = Directory.listDirector(root)
        var mutableListOf = mutableListOf<FileAttribute>()
        for (file in listDirector) {
            file.toFileAttribute()?.let { mutableListOf.add(it) }
        }
        return mutableListOf;
    }

    override fun getFileIcon(path: String): ByteArrayResource {
        /**
         * if path is directory
         */
        if (Paths.get(path).isDirectory()) {
            var classPathResource = ClassPathResource(FileIconRegister.getFullPath("folder"))
            return ByteArrayResource(classPathResource.inputStream.readBytes())
        }
        var fileAttribute = Paths.get(path).toFileAttribute()
        fileAttribute?.let {
            if (fileAttribute.type == "img") {
                var bufferedOutputStream = ByteArrayOutputStream()
                Thumbnails.of(path)
                        .outputQuality(0.5)
                        .scale(0.5)
                        .toOutputStream(bufferedOutputStream)
                return ByteArrayResource(bufferedOutputStream.toByteArray())
            }
            var classPathResource = ClassPathResource(FileIconRegister.getFullPath(fileAttribute.type))
            if (classPathResource.exists()) {
                return ByteArrayResource(classPathResource.inputStream.readBytes())
            }
        }
        /**
         * return default icon
         */
        var classPathResource = ClassPathResource(FileIconRegister.getFullPath("file"))
        return ByteArrayResource(classPathResource.inputStream.readBytes())
    }
}