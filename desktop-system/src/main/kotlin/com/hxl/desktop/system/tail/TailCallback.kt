package com.hxl.desktop.system.tail

fun interface TailCallback {
    fun line(line: String)
}