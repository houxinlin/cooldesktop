package com.hxl.desktop.file.bean




/**
* @description: 文件属性
* @date: 2022/9/6 下午10:50
*/

data class FileAttribute(
    var path: String = "",
    var type: String = "",
    var fileSize: Long = -1,
    var name: String = "",
    var rawType: String = "",
    var createTimer: Long,
    var lastAccessTime: Long,
    var lastModifiedTime: Long,
    var owner: String,
    var isTextType: Boolean = false,
    var mimeType: String,
    var isFile: Boolean = true,
    var parent: String = ""
)
