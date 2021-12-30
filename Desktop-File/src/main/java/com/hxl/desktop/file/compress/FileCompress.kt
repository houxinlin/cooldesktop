package com.hxl.desktop.file.compress

import com.hxl.desktop.common.extent.toFile
import com.hxl.desktop.common.extent.toPath
import com.hxl.desktop.common.extent.walkFileTree
import org.apache.commons.compress.archivers.ArchiveOutputStream
import org.apache.commons.compress.archivers.ArchiveStreamFactory
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import org.apache.commons.compress.utils.IOUtils
import org.apache.tomcat.util.http.fileupload.FileUtils
import java.io.*
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.isDirectory
import kotlin.io.path.name


class FileCompress {
    companion object {
        fun getCompressByType(type: String): ICompress {
            if (type.lowercase() == "zip") {
                return ZipCompress();
            }
            return null!!
        }

        fun createArchiveOutputStream(path: String, targetName: String, type: String): ArchiveOutputStream {
            var name = Paths.get(path.toFile().parent, "$targetName.$type").toFile();
            return ArchiveStreamFactory().createArchiveOutputStream(type, FileOutputStream(name))
        }
    }

    class ZipCompress : ICompress {
        override fun compress(path: String, targetName: String) {
            var archiveOutputStream = createArchiveOutputStream(path, targetName, ArchiveStreamFactory.ZIP);
            if (!Files.isDirectory(Paths.get(path))) {
                var zipArchiveEntry = ZipArchiveEntry(Paths.get(path).name)
                archiveOutputStream.putArchiveEntry(zipArchiveEntry)
                IOUtils.copy(Files.newInputStream(Paths.get(path)), archiveOutputStream);
                archiveOutputStream.closeArchiveEntry()
                archiveOutputStream.finish()
                archiveOutputStream.close()
                return
            }
            for (file in Paths.get(path).walkFileTree()) {
                val entryName: String = file.removePrefix(path)
                val entry = ZipArchiveEntry(entryName)
                if (!file.toPath().isDirectory()) {
                    archiveOutputStream.putArchiveEntry(entry)
                    IOUtils.copy(file.toFile().inputStream(), archiveOutputStream)
                } else {
                    var zipArchiveEntry = ZipArchiveEntry(file.removePrefix(path) + "/")
                    archiveOutputStream.putArchiveEntry(zipArchiveEntry)
                }
                archiveOutputStream.closeArchiveEntry()
            }

            archiveOutputStream.finish()
            archiveOutputStream.close()
        }
    }

    class Z7Compress : ICompress {
        override fun compress(path: String, targetName: String) {

        }
    }

    class TarCompress : ICompress {
        override fun compress(path: String, targetName: String) {

        }
    }
}
