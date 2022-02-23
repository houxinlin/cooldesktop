package com.hxl.desktop.file.compress

interface ICompress {
    /**
     *
     * 压缩
     */
    fun compress(path: String, targetName: String)

    /**
     * 解压
     */
    fun decompression(path: String);
}