package com.hxl.desktop.file.compress

import com.hxl.desktop.common.extent.toFile
import com.hxl.desktop.common.extent.toPath
import com.hxl.desktop.file.extent.walkFileTree
import org.apache.commons.compress.archivers.ArchiveEntry
import org.apache.commons.compress.archivers.ArchiveStreamFactory
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry
import org.apache.commons.compress.archivers.sevenz.SevenZFile
import org.apache.commons.compress.compressors.CompressorStreamFactory
import org.apache.commons.compress.utils.IOUtils
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.outputStream


abstract class Compressed : ICompress {

    private fun getOutputStream(type: String, outPath: String): BaseArchiveOutputStream<*, *> {
        if ("tar" == type)
            return CTarArchiveOutputStream(outPath)
        if ("7z" == type)
            return CSevenArchiveOutputStream(outPath);
        if ("zip" == type)
            return CZipArchiveOutputStream(outPath);
        return null!!;
    }

    fun compressByType(path: String, targetName: String, type: String) {
        var targetArchivePath = Paths.get(path.toFile().parent, "$targetName.$type").toFile();
        var archiveOutputStream = getOutputStream(type, targetArchivePath.toString())

        if (!Files.isDirectory(Paths.get(path))) {
            var archiveName = path.removePrefix(path)
            archiveOutputStream.putArchiveEntry(archiveName, path)
            archiveOutputStream.close()
            return
        }
        for (file in path.toPath().walkFileTree()) {
            if (!file.toPath().isDirectory()) {
                var archiveName = file.removePrefix(path)
                archiveOutputStream.putArchiveEntry(archiveName, file)
            } else {
                var archiveEntry = file.removePrefix(path) + "/"
                archiveOutputStream.putArchiveEntry(archiveEntry, file)
            }
        }
        archiveOutputStream.close()
    }

    fun getUnCompressName(name: String): String {
        if (name.endsWith(".")) {
            return name
        }
        return name.substring(0, name.lastIndexOf("."));
    }

    fun findNextIndexFileName(path: String, name: String): String {
        var unCompressName = getUnCompressName(name)
        if (Paths.get(path, name).exists()) {
            var count = 1;
            while (Paths.get(path, "${name}($count)").exists()) {
                count++
            }
            return "${name}($count)"
        }
        return unCompressName
    }

    fun decompress(path: String) {
        var inputStream: InputStream? = null;
        var parentPath = path.toFile().parent.toString();
        try {
            inputStream = CompressorStreamFactory().createCompressorInputStream(path.toFile().inputStream().buffered())
            inputStream = inputStream.buffered()
        } catch (e: Exception) {
        }
        if (inputStream == null) {
            inputStream = path.toFile().inputStream().buffered()
        }
        var archiveStream = ArchiveStreamFactory().createArchiveInputStream(inputStream)

        var archiveEntry: ArchiveEntry? = null
        var targetDirectorName = findNextIndexFileName(parentPath, path.toPath().last().toString())
        while (archiveStream.nextEntry.also { archiveEntry = it } != null) {
            var itemName = archiveEntry!!.name
            var curPaths = Paths.get(parentPath, targetDirectorName, itemName)
            if (archiveEntry!!.isDirectory) {
                if (!curPaths.exists()) {
                    curPaths.createDirectories()
                }
                continue
            }
            if (!curPaths.toFile().parent.toFile().exists()) {
                Files.createDirectories(Paths.get(curPaths.toFile().parent))
            }
            IOUtils.copy(archiveStream, curPaths.outputStream().buffered())
        }
    }

}

class TarCompress : Compressed() {
    override fun compress(path: String, targetName: String) {
        super.compressByType(path, targetName, "tar")
    }

    override fun decompression(path: String) {
        super.decompress(path)
    }
}

class ZipCompress : Compressed() {
    override fun compress(path: String, targetName: String) {
        super.compressByType(path, targetName, "zip")
    }

    override fun decompression(path: String) {
        super.decompress(path)
    }
}


class SEVENZCompress : Compressed() {
    override fun compress(path: String, targetName: String) {
        super.compressByType(path, targetName, "7z")
    }

    override fun decompression(path: String) {
        try {
            SevenZFile(File(path)).use {
                var sevenZArchiveEntry: SevenZArchiveEntry?
                var parentPath = path.toFile().parent.toString();
                var targetDirectorName = findNextIndexFileName(parentPath, path.toPath().last().toString())
                while (it.nextEntry.also { sevenZArchiveEntry = it } != null) {
                    var itemName = sevenZArchiveEntry!!.name
                    var curPaths = Paths.get(parentPath, targetDirectorName, itemName)
                    if (sevenZArchiveEntry!!.isDirectory) {
                        if (!curPaths.exists()) {
                            curPaths.createDirectories()
                        }
                        continue
                    }
                    val content = ByteArray(sevenZArchiveEntry!!.size.toInt())
                    it.read(content)
                    if (!curPaths.toFile().parent.toFile().exists()) {
                        Files.createDirectories(Paths.get(curPaths.toFile().parent))
                    }
                    Files.write(curPaths, content)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}