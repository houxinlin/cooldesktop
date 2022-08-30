package com.hxl.desktop.common.kotlin.extent

import com.hxl.desktop.common.kotlin.extent.StringExtent.Companion.intToChar
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

class StringExtent {
    companion object{
        val intToChar = mutableMapOf<Int, Char>()
        init {
            for (i in 0..9) intToChar[i] = Char(i + 48)
            for (i in 10..35) intToChar[i] = Char(i + 55)
            for (i in 36..61) intToChar[i] = Char(i + 61)
        }
    }
}
private fun String.toGroupWhitLength(size: Int, delimiter: String): String {
    if (this.isEmpty()) return ""
    val stringBuffer = StringBuffer()
    for ((count, c) in this.toCharArray().withIndex()) {
        stringBuffer.append(c)
        if (((count + 1)) % size == 0) stringBuffer.append(delimiter)
    }
    return  stringBuffer.removeSuffix(",").toString()
}
fun String.shortUrlToHasCode(): Int {
    var target =this;
    val str = StringBuffer()
    if (target.startsWith("0")) {
        str.append("-")
        target =this.substring(1)
    }
    for (c in target.toCharArray()) {
        for (key in intToChar.keys) {
            if (intToChar[key] == c) {
                str.append(key)
            }
        }
    }
    return str.toString().toInt()
}
fun String.mapToShortArg(delimiter: String = ","): String {

    val split = this.toGroupWhitLength(2,delimiter).split(delimiter)
    val result = StringBuffer()

    for (item in split) {
        //负数转换为0开头的
        if (item.startsWith("-")) {
            result.append("0")
            result.append(item.toCharArray()[1].toString().toInt())
            continue
        }
        if (Integer.valueOf(item) > 61) {
            val subSplite = item.toGroupWhitLength(1, ",").split(",")
            for (subItem in subSplite) {
                result.append(intToChar[Integer.valueOf(subItem)])
            }
            continue
        }
        result.append(intToChar[Integer.valueOf(item)])
    }
    return result.toString()
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