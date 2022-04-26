package com.hxl.desktop.common.utils

import com.hxl.desktop.common.extent.fillZero

object VersionUtils {
    fun isLz(v1: String, v2: String): Int {
        var v1Split = v1.split(".").toMutableList()
        var v2Split = v2.split(".").toMutableList()

        var max = v1Split.size.coerceAtLeast(v2Split.size)
        v1Split.fillZero(max)
        v2Split.fillZero(max)
        var index = 0;
        while (index < max) {
            if (v2Split[index].toInt() == v1Split[index].toInt()) {
                index++
                continue
            }
            if (v2Split[index].toInt() > v1Split[index].toInt()) return -1
            if (v2Split[index].toInt() < v1Split[index].toInt()) return 1
        }

        return 0
    }
}