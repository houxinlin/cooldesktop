package com.hxl.desktop.common.utils

object StringUtils {
    private const val STR="qwertyuiopasdfghkzxcvbnm123456789QQWERTYUIOPASDFGHJKLZXCVBNM"
    fun randomString(size: Int): String {
        val result = StringBuffer()
        for (i in 0 until size) {
            result.append(STR.random())
        }
        return result.toString()
    }
}