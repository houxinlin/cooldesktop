package com.hxl.desktop.common.extent


fun MutableList<String>.fillZero(max: Int) {
    if (this.size != max) {
        for (i in 0 until max - this.size) this.add("0")
    }
}