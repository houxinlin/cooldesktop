package com.hxl.desktop.file.utils

import com.hxl.desktop.file.bean.FileAttribute
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Collectors
import kotlin.io.path.exists

/**
 * @author:   HouXinLin
 * @email:    2606710413@163.com
 * @date:     20212021/12/18
 * @describe: Director utils
 * @version:  v1.0
 */
object Directory {

    /**
     * list director
     */
    fun listDirector(root: String): List<Path> {
        if (!Paths.get(root).exists()) return emptyList()
        return Files.list(Paths.get(root)).collect(Collectors.toList())
    }

    fun getFileSize(path: String): Long {
        return if (File(path).isDirectory) {
            -1;
        } else {
            File(path).length();
        }
    }

    fun getFileAttribute(path: Path): FileAttribute {
        return FileAttribute(path.toString(), getFileType(path.toString()), getFileSize(path.toString()), path.last().toString())
    }

    fun getFileType(path: String): String {
        return if (File(path).isDirectory) {
            "directory"
        } else {
            var suffix = path.substring(path.lastIndexOf(".") + 1)
            if (FileIconRegister.IMAGE.contains(suffix.lowercase())) {
                return "img";
            }
            if (suffix.isEmpty()) {
                return "file"
            }
            return suffix;
        }
    }
}
