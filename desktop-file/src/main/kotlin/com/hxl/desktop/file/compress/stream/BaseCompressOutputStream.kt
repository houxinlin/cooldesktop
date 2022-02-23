package com.hxl.desktop.file.compress.stream

import common.extent.toFile
import org.apache.commons.compress.archivers.ArchiveEntry
import org.apache.commons.compress.archivers.ArchiveOutputStream
import java.nio.file.Files
import java.nio.file.Paths

abstract class BaseCompressOutputStream(var outPath: String) :
    BaseArchiveOutputStream<ArchiveOutputStream, ArchiveEntry> {
    private var outputStream: ArchiveOutputStream;
    abstract fun getCompressName(): String;
    abstract fun getAlgorithmName(): String;

    abstract fun createOutputStream(): ArchiveOutputStream

    init {
        outputStream = createOutputStream()
    }


    override fun close() {
        outputStream.finish()
        outputStream.close()
    }

    override fun createEntry(name: String, file: String): ArchiveEntry {
        return outputStream.createArchiveEntry(file.toFile(), name)
    }

    override fun putArchiveEntry(name: String, file: String) {
        outputStream.putArchiveEntry(createEntry(name, file))
        if (!Files.isDirectory(Paths.get(file))) {
            outputStream.write(file.toFile().readBytes())
        }
        outputStream.closeArchiveEntry()
    }


}