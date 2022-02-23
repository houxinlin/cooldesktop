package com.hxl.desktop.file.utils

object ClassPathUtils {
    private const val PREFIX = "/static/icon/ic-";
    fun getClassPathFullPath(key: String): String {
        return "${PREFIX}${key}.png";
    }
}