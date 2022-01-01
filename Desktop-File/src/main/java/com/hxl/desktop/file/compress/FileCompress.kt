package com.hxl.desktop.file.compress

import com.hxl.desktop.common.extent.toFile
import com.hxl.desktop.common.extent.toPath
import com.hxl.desktop.file.extent.walkFileTree
import org.apache.commons.compress.archivers.ArchiveStreamFactory
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.isDirectory


class FileCompress {
    companion object {
        fun getCompressByType(type: String): ICompress {
            if ("zip" == type.lowercase()) {
                return ZipCompress();
            }
            if ("tar" == type.lowercase()) {
                return TarCompress()
            }
            if ("7z" == type.lowercase()) {
                return SEVEN_ZCompress();
            }
            return null!!
        }

        private fun putFileItemToArchive(name: String, path: String, outputStream: BaseArchiveOutputStream<*, *>) {
            outputStream.putArchiveEntry(name, path)
        }

        private fun getOutputStream(type: String, outPath: String): BaseArchiveOutputStream<*, *> {
            if ("tar" == type) {
                return CTarArchiveOutputStream(outPath)
            }
            if ("7z" == type) {
                return CSevenArchiveOutputStream(outPath);
            }
            return CZipArchiveOutputStream(outPath)
        }

        fun compressByType(path: String, targetName: String, type: String) {
            var targetArchivePath = Paths.get(path.toFile().parent, "$targetName.$type").toFile();
            var archiveOutputStream = getOutputStream(type, targetArchivePath.toString())

            if (!Files.isDirectory(Paths.get(path))) {
                var archiveName = path.removePrefix(path)
                putFileItemToArchive(archiveName, path, archiveOutputStream)
                archiveOutputStream.close()
                return
            }
            for (file in path.toPath().walkFileTree()) {
                if (!file.toPath().isDirectory()) {
                    var archiveName = file.removePrefix(path)
                    putFileItemToArchive(archiveName, file, archiveOutputStream)
                } else {
                    var archiveEntry = file.removePrefix(path) + "/"
                    putFileItemToArchive(archiveEntry, file, archiveOutputStream)
                }
            }
            archiveOutputStream.close()
        }
    }

    class ZipCompress : ICompress {
        override fun compress(path: String, targetName: String) {
            compressByType(path, targetName, ArchiveStreamFactory.ZIP)
        }
    }

    class SEVEN_ZCompress : ICompress {
        override fun compress(path: String, targetName: String) {
            compressByType(path, targetName, ArchiveStreamFactory.SEVEN_Z)
        }
    }

    class TarCompress : ICompress {
        override fun compress(path: String, targetName: String) {
            compressByType(path, targetName, ArchiveStreamFactory.TAR)
        }
    }
}
