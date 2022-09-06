package com.hxl.desktop.file.compress

import com.hxl.desktop.file.compress.stream.CZipArchiveOutputStream
import java.io.File
import java.nio.file.Paths


class ZipCompress : Compressed() {
    override fun compress(path: String, targetName: String) {
        val parent = File(path).parent
        super.compressByType(path, CZipArchiveOutputStream(Paths.get(parent, targetName).toString()))
    }

    override fun decompression(path: String) {
        super.decompress(path)
    }
}
