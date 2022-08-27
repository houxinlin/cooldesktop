package com.hxl.desktop.common.kotlin.extent

import com.hxl.desktop.common.model.Page


fun MutableList<String>.fillZero(max: Int) {
    if (this.size != max) {
        for (i in 0 until max - this.size) this.add("0")
    }
}

fun <T> List<T>.toPage(size: Int = 50, page: Int = 1): Page<T> {
    val emptyPage = Page<T>().apply { total = 0 }
    val preIndex = (size * (page - 1))
    val dataSize = this.size
    val fromIndex = if (preIndex < 0) 0 else preIndex
    if (fromIndex > this.size) return emptyPage
    val toIndex = if (fromIndex + size > this.size) fromIndex + (this.size - fromIndex) else fromIndex + size
    val datas = this.subList(fromIndex, toIndex)
    return Page<T>().apply {
        this.total = dataSize.toLong()
        this.size=size
        this.datas = datas
        this.current=page
    }
}
