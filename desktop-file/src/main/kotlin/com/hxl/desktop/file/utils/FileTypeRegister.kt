package com.hxl.desktop.file.utils

import com.hxl.desktop.file.emun.FileType

/**
 * @author:   HouXinLin
 * @email:    2606710413@qq.com
 * @date:     20212021/12/18
 * @describe:
 * @version:  v1.0
 */
object FileTypeRegister {
    val TEXT_SUFFIX_LIST = mutableListOf<String>(
        "txt",
        "ini",
        "conf",
        "json",
        "xml",
        "html",
        "js",
        "css",
        "java",
        "py",
        "bat",
        "sh",
        "ink",
        "out"
    )
    val IMAGE_SUFFIX_LIST = mutableListOf<String>("png", "jpg", "jpeg", "ico", "webp", "bmp")
    val VIDEO_SUFFIX_LIST = mutableListOf<String>("flv", "mp4", "m3u8", "ts", "3gp", "mov", "avi", "wmv")
    val MUSIC_SUFFIX_LIST = mutableListOf<String>("mp3", "aifc", "m3u", "ra", "wav", "ram")

    fun getFileTypeBySuffix(value: String): String {
        if (TEXT_SUFFIX_LIST.contains(value.lowercase())) {
            return FileType.TEXT.typeName
        }
        if (IMAGE_SUFFIX_LIST.contains(value.lowercase())) {
            return FileType.IMAGE.typeName
        }
        if (VIDEO_SUFFIX_LIST.contains(value.lowercase())) {
            return FileType.TEXT.typeName
        }
        if (MUSIC_SUFFIX_LIST.contains(value.lowercase())) {
            return FileType.TEXT.typeName
        }
        return FileType.NONE.typeName;

    }
}