package com.hxl.desktop.file.compress.stream

import com.hxl.desktop.common.kotlin.extent.toFile
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile
import java.nio.file.Files
import java.nio.file.Paths

class CSevenArchiveOutputStream(outPath: String) : BaseArchiveOutputStream<SevenZOutputFile, SevenZArchiveEntry> {
    private  var outputStream: SevenZOutputFile;

    init {
        outputStream = SevenZOutputFile(outPath.toFile())
    }

    override fun close() {
        outputStream.finish()
        outputStream.close()
    }

    override fun createEntry(name: String, file: String): SevenZArchiveEntry {
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
