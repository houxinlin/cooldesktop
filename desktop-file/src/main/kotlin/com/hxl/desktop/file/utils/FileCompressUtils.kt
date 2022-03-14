package com.hxl.desktop.file.utils

import common.extent.toFile
import com.hxl.desktop.file.compress.*
import org.apache.commons.compress.archivers.ArchiveStreamFactory
import org.apache.commons.compress.compressors.CompressorStreamFactory


class FileCompressUtils {
    companion object {
        fun getCompressByType(type: String): ICompress? {
            if ("zip" == type.lowercase()) return ZipCompress();
            if ("tar" == type.lowercase()) return TarCompress()
            if ("xz" == type.lowercase()) return TarCompress();
            if ("7z" == type.lowercase()) return SEVENZCompress();
            return null
        }

        fun getFileType(path: String): String {
            try {
                var compressByType = ArchiveStreamFactory.detect(path.toFile().inputStream().buffered())
                if (compressByType != null) return compressByType
            } catch (e: Exception) {
                return CompressorStreamFactory.detect(path.toFile().inputStream().buffered())
            }
            return ""
        }

    }


}
