package com.hxl.desktop.file.utils

import com.hxl.desktop.common.extent.toFile
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
        val canThumbnailsType = arrayOf("jpg", "jpeg", "bmp")
        val notCanThumbnailsType = arrayOf("svg")
        val imageFile = path.toFile()
        for (itemType in canThumbnailsType) {
            if (imageFile.name.lowercase().endsWith(itemType)) {
                try {
                    var bufferedOutputStream = ByteArrayOutputStream()
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
        for (itemType in notCanThumbnailsType) {
            if (imageFile.name.lowercase().endsWith(itemType)) {
                return null;
            }
        }
        return getImageByteArrayResource(path);
    }

    private fun getImageByteArrayResource(path: String): ByteArrayResource {
        return ByteArrayResource(Files.readAllBytes(Paths.get(path)))
    }
}

