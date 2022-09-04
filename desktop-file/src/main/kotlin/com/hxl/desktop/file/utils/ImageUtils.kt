package com.hxl.desktop.file.utils

import com.hxl.desktop.common.kotlin.extent.toFile
import net.coobird.thumbnailator.Thumbnails
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.io.ByteArrayResource
import java.io.ByteArrayOutputStream
import java.nio.file.Files
import java.nio.file.Paths

object ImageUtils {
    private val log: Logger = LoggerFactory.getLogger(ImageUtils::class.java)
    fun thumbnails(path: String): ByteArrayResource? {
        val canCompressType = arrayOf("jpg", "jpeg", "bmp")
        val notCanCompressType = arrayOf("svg","xpm")
        val imageFile = path.toFile()

        var flag =false
        for (itemType in canCompressType.plus(notCanCompressType)){
            if (imageFile.name.lowercase().endsWith(itemType)) flag=true
        }
        if (!flag) return  null
        for (itemType in canCompressType) {
            if (imageFile.name.lowercase().endsWith(itemType)) {
                try {
                    val bufferedOutputStream = ByteArrayOutputStream()
                    Thumbnails.of(path)
                        .outputQuality(0.3)
                        .scale(0.3)
                        .toOutputStream(bufferedOutputStream)
                    return ByteArrayResource(bufferedOutputStream.toByteArray())
                } catch (e: Exception) {
                    log.warn("获取缩略图失败,{}" + e.message)
                }
            }
        }
        for (itemType in notCanCompressType) {
            if (imageFile.name.lowercase().endsWith(itemType))    return null
        }
        return getImageByteArrayResource(path);
    }

    private fun getImageByteArrayResource(path: String): ByteArrayResource {
        return ByteArrayResource(Files.readAllBytes(Paths.get(path)))
    }
}

