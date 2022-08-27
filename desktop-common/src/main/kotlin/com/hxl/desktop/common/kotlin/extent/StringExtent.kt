package com.hxl.desktop.common.kotlin.extent

import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

class StringExtent {
}

fun String.toPath(): Path {
    return Paths.get(this);
}

fun String.toFile(): File {
    return File(this);
}

fun String.randomString(size: Int): String {
    val result = StringBuffer()
    for (i in 0 until size) {
        result.append(this.random())
    }
    return result.toString()
}