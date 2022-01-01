package com.hxl.desktop.file.compress

import com.hxl.desktop.common.extent.toFile
import org.apache.commons.compress.archivers.ArchiveEntry
import org.apache.commons.compress.archivers.ArchiveOutputStream
import org.apache.commons.compress.archivers.ArchiveStreamFactory
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile
import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import org.apache.commons.compress.compressors.CompressorStreamFactory
import org.apache.commons.compress.utils.IOUtils
import java.io.FileOutputStream
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.Paths

interface BaseArchiveOutputStream<T, E> {
    fun writeByte(data: ByteArray)
    fun getOutputStream(): T
    fun putArchiveEntry(name: String, file: String)
    fun createEntry(name: String, file: String): E
    fun close()
}

abstract class BaseCompressOutputStream(outPath: String) : BaseArchiveOutputStream<ArchiveOutputStream, ArchiveEntry> {
    private lateinit var outputStream: ArchiveOutputStream;
    abstract fun getCompressName(): String;

    init {
        if ("tar" == getCompressName()) {
            var xz = CompressorStreamFactory().createCompressorOutputStream(
                CompressorStreamFactory.XZ,
                FileOutputStream(outPath)
            )
            outputStream = ArchiveStreamFactory().createArchiveOutputStream(getCompressName(), xz)
        } else {
            var gzip = CompressorStreamFactory().createCompressorOutputStream(
                CompressorStreamFactory.GZIP,
                FileOutputStream(outPath)
            )
            outputStream = ArchiveStreamFactory()
                .createArchiveOutputStream(getCompressName(), gzip)
        }
    }

    override fun getOutputStream(): ArchiveOutputStream {
        return outputStream
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


    override fun writeByte(data: ByteArray) {
        outputStream.write(data)
    }
}

class CSevenArchiveOutputStream(outPath: String) : BaseArchiveOutputStream<SevenZOutputFile, SevenZArchiveEntry> {
    private lateinit var outputStream: SevenZOutputFile;

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

    override fun getOutputStream(): SevenZOutputFile {
        return outputStream
    }

    override fun writeByte(data: ByteArray) {
        outputStream.write(data)
    }
}

class CTarArchiveOutputStream(outPath: String) : BaseCompressOutputStream(outPath) {
    override fun getCompressName(): String {
        return "tar"
    }
}

class CZipArchiveOutputStream(outPath: String) : BaseCompressOutputStream(outPath) {
    override fun getCompressName(): String {
        return "zip"
    }
}