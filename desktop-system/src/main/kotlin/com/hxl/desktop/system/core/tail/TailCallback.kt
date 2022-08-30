package com.hxl.desktop.system.core.tail

fun interface TailCallback {
    fun line(line: String)
}