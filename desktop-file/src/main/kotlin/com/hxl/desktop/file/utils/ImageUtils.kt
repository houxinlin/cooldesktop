package com.hxl.desktop.file.utils

import common.extent.toFile
import net.coobird.thumbnailator.Thumbnails
import org.springframework.core.io.ByteArrayResource
import java.io.ByteArrayOutputStream
import java.nio.file.Files
import java.nio.file.Paths

object ImageUtils {
    fun thumbnails(path: String): ByteArrayResource? {
        var canThumbnailsType = arrayOf("jpg", "jpeg", "bmp")
        var notCanThumbnailsType = arrayOf("svg")
        var imageFile = path.toFile()
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

