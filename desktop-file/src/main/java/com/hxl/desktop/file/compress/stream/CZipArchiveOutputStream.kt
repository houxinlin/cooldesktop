package com.hxl.desktop.file.compress.stream

import org.apache.commons.compress.archivers.ArchiveOutputStream
import org.apache.commons.compress.archivers.ArchiveStreamFactory
import org.apache.commons.compress.compressors.CompressorStreamFactory
import java.io.BufferedOutputStream
import java.io.FileOutputStream

class CZipArchiveOutputStream(outPath: String) : BaseCompressOutputStream(outPath) {
    override fun createOutputStream(): ArchiveOutputStream {
        return ArchiveStreamFactory().createArchiveOutputStream(
            getCompressName(),
            BufferedOutputStream(FileOutputStream(outPath))
        )
    }

    override fun getCompressName(): String {
        return ArchiveStreamFactory.ZIP
    }

    override fun getAlgorithmName(): String {
        return CompressorStreamFactory.DEFLATE
    }
}