package com.hxl.desktop.common.utils

object LanguageUtils {
    fun doWhile(function: () -> Unit, count: Int = -1) {
        if (count == -1) {
            while (true) {
                function.invoke()
            }
        }
        for (i in 0 until count) {
            function.invoke()
        }
    }
}