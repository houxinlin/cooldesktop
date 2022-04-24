package com.hxl.desktop.common.utils

object VersionUtils {
    fun isLz(v1: String, v2: String): Int {
        var v1Split = v1.split(".")
        var v2Split = v2.split(".")
        var min = v1Split.size.coerceAtMost(v2Split.size)
        var index = 0;
        while (index < min) {
            if (v2Split[index] == v1Split[index]) {
                index++
                continue
            }
            if (v2Split[index] > v1Split[index]) {
                return -1
            }
            if (v2Split[index] < v1Split[index]) {
                return 1
            }
            index++
        }
        return 0
    }
}