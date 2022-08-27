package com.hxl.desktop.common.model

/**
 * 分页
 */
class Page<T> {
    var total: Long = 0

    var datas: List<T> = arrayListOf()

    var size: Int = 0

    var current: Int = 0
}