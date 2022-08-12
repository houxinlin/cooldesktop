package com.hxl.desktop.file.bean


/**
 * @author:   HouXinLin
 * @email:    2606710413@qq.com
 * @date:     20212021/12/18
 * @describe: file attribute
 * @version:  v1.0
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
