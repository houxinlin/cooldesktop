package com.hxl.desktop.file.utils

import net.sf.jmimemagic.Magic

object FileUtils {
    fun getFileMimeType(data: ByteArray): String {
        var magicMatch = Magic.getMagicMatch(data, true)
        return magicMatch.mimeType
    }
}