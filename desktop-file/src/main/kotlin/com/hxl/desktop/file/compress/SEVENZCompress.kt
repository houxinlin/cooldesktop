package com.hxl.desktop.file.compress

import com.hxl.desktop.common.kotlin.extent.toFile
import com.hxl.desktop.common.kotlin.extent.toPath
import com.hxl.desktop.file.compress.stream.CSevenArchiveOutputStream
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry
import org.apache.commons.compress.archivers.sevenz.SevenZFile
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.createDirectories
import kotlin.io.path.exists

/**
 * 7z compress
 */
class SEVENZCompress() : Compressed() {
    override fun compress(path: String, targetName: String) {
        val parent = File(path).parent
        super.compressByType(path, CSevenArchiveOutputStream(Paths.get(parent, targetName).toString()))
    }

    override fun decompression(path: String) {
        SevenZFile(File(path)).use {
            var sevenZArchiveEntry: SevenZArchiveEntry?
            val parentPath = path.toFile().parent.toString();
            val targetDirectorName = getArchiveNameByIndex(parentPath, path.toPath().last().toString())
            while (it.nextEntry.also { sevenZArchiveEntry = it } != null) {
                val itemName = sevenZArchiveEntry!!.name
                val curPaths = Paths.get(parentPath, targetDirectorName, itemName)
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
    }
}