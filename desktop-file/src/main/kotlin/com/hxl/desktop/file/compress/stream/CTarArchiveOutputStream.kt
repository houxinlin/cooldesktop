package com.hxl.desktop.file.compress.stream

import org.apache.commons.compress.archivers.ArchiveOutputStream
import org.apache.commons.compress.archivers.ArchiveStreamFactory
import org.apache.commons.compress.compressors.CompressorStreamFactory
import java.io.BufferedOutputStream
import java.io.FileOutputStream

class CTarArchiveOutputStream(outPath: String) : BaseCompressOutputStream(outPath) {
    override fun createOutputStream(): ArchiveOutputStream {
        var compressorOutputStream =
            CompressorStreamFactory().createCompressorOutputStream(
                getAlgorithmName(),
                BufferedOutputStream(FileOutputStream(outPath))
            )
        return ArchiveStreamFactory().createArchiveOutputStream(getCompressName(), compressorOutputStream)
    }

    override fun getCompressName(): String {
        return ArchiveStreamFactory.TAR
    }

    override fun getAlgorithmName(): String {
        return CompressorStreamFactory.GZIP
    }
}
