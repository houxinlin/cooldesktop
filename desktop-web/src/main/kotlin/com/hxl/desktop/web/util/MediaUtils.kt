package com.hxl.desktop.web.util

import org.springframework.http.MediaType

object MediaUtils {
    private val mediaType = mutableMapOf<String, MediaType>();

    init {
        mediaType["html"] = MediaType.TEXT_HTML
        mediaType["css"] = MediaType.parseMediaType("text/css")
        mediaType["js"] = MediaType.parseMediaType("application/javascript")
        mediaType["gif"] = MediaType.IMAGE_GIF
        mediaType["png"] = MediaType.IMAGE_PNG
        mediaType["jpeg"] = MediaType.IMAGE_JPEG
        mediaType["bmp"] = MediaType.parseMediaType("image/bmp")
        mediaType["webp"] = MediaType.parseMediaType("image/webp")
        mediaType["x-icon"] = MediaType.parseMediaType("image/x-icon")
        mediaType["icon"] = MediaType.parseMediaType("image/icon")
        mediaType["bmp"] = MediaType.parseMediaType("image/bmp")
    }

    fun getFileMimeType(value: String): MediaType {
        for (key in mediaType.keys) {
            if (value.lowercase().endsWith(key)) {
                return mediaType[key]!!
            }
        }
        return MediaType.parseMediaType(MediaType.APPLICATION_OCTET_STREAM_VALUE)
    }
}