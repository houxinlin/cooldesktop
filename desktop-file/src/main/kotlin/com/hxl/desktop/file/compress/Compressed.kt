package com.hxl.desktop.file.compress

import com.hxl.desktop.common.extent.toFile
import com.hxl.desktop.common.extent.toPath
import com.hxl.desktop.file.compress.stream.BaseArchiveOutputStream
import com.hxl.desktop.file.extent.walkFileTree
import org.apache.commons.compress.archivers.ArchiveEntry
import org.apache.commons.compress.archivers.ArchiveStreamFactory
import org.apache.commons.compress.compressors.CompressorStreamFactory
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.isDirectory


abstract class Compressed() : ICompress {

    fun compressByType(path: String, archiveOutputStream: BaseArchiveOutputStream<*, *>) {
        val compressfile = File(path)
        if (compressfile.isFile) {
            archiveOutputStream.putArchiveEntry(compressfile.name, path)
            archiveOutputStream.close()
            return
        }
        for (fileItem in path.toPath().walkFileTree()) {
            if (!fileItem.isDirectory()) {
                val archiveName = fileItem.toString().removePrefix(path)
                archiveOutputStream.putArchiveEntry(archiveName, fileItem.toString())
            } else {
                val archiveEntry = fileItem.toString().removePrefix(path) + "/"
                archiveOutputStream.putArchiveEntry(archiveEntry, fileItem.toString())
            }
        }
        archiveOutputStream.close()
    }

    private fun getDefaultName(name: String): String {
        if (name.endsWith(".")) {
            return name
        }
        return name.substring(0, name.lastIndexOf("."));
    }

    fun getArchiveNameByIndex(parent: String, name: String): String {
        val unCompressName = getDefaultName(name)
        if (Paths.get(parent, unCompressName).exists()) {
            var count = 1;
            while (Paths.get(parent, "${unCompressName}($count)").exists()) {
                count++
            }
            return "${unCompressName}($count)"
        }
        return unCompressName
    }

    fun decompress(path: String) {
        var inputStream: InputStream? = null;
        val parentPath = path.toFile().parent.toString();
        try {
            inputStream = CompressorStreamFactory().createCompressorInputStream(path.toFile().inputStream().buffered())
            inputStream = inputStream.buffered()
        } catch (e: Exception) {
        }
        if (inputStream == null) {
            inputStream = path.toFile().inputStream().buffered()
        }
        val archiveStream = ArchiveStreamFactory().createArchiveInputStream(inputStream)

        var archiveEntry: ArchiveEntry?
        val targetDirectorName = getArchiveNameByIndex(parentPath, path.toPath().last().toString())
        while (archiveStream.nextEntry.also { archiveEntry = it } != null) {
            val itemName = archiveEntry!!.name
            val curPaths = Paths.get(parentPath, targetDirectorName, itemName)
            if (archiveEntry!!.isDirectory) {
                if (!curPaths.exists()) {
                    curPaths.createDirectories()
                }
                continue
            }
            if (!curPaths.toFile().parent.toFile().exists()) {
                Files.createDirectories(Paths.get(curPaths.toFile().parent))
            }
            Files.write(curPaths, archiveStream.readBytes())
        }
    }

}