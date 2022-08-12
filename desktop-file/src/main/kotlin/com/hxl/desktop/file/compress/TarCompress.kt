package com.hxl.desktop.file.compress

import com.hxl.desktop.file.compress.stream.CTarArchiveOutputStream
import java.io.File
import java.nio.file.Paths

class TarCompress : Compressed() {
    override fun compress(path: String, targetName: String) {
        val parent = File(path).parent
        super.compressByType(path, CTarArchiveOutputStream(Paths.get(parent, targetName).toString()))
    }

    override fun decompression(path: String) {
        super.decompress(path)
    }
}