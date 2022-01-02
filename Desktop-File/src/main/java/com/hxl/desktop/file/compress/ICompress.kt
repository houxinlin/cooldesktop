package com.hxl.desktop.file.compress

interface ICompress {
    fun compress(path: String, targetName: String)

    fun decompression(path: String);
}