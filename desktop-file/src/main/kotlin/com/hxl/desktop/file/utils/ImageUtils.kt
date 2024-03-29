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
        val canCompressType = arrayOf("jpg", "jpeg", "bmp") //能压缩的
        val notCanCompressType = arrayOf("svg","xpm","png") //不能压缩的
        val imageFile = path.toFile()

        var flag =false
        for (itemType in canCompressType.plus(notCanCompressType)){//其余可能是类型检测出现问题，并不是图片
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

        return getImageByteArrayResource(path);
    }

    private fun getImageByteArrayResource(path: String): ByteArrayResource {
        return ByteArrayResource(Files.readAllBytes(Paths.get(path)))
    }
}

