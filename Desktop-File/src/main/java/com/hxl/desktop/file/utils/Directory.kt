package com.hxl.desktop.file.utils

import com.hxl.desktop.file.bean.FileAttribute
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.Collectors

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
    fun listDirector(root: String): List<FileAttribute> {
        var files = Files.list(Paths.get(root)).collect(Collectors.toList())
        var mutableListOf = mutableListOf<FileAttribute>()
        for (file in files) {
            mutableListOf.add(
                    FileAttribute().apply {
                        path = file.toString();
                        type = getFileType(file.toString())
                        fileSize = getFileSize(file.toString())
                        name=file.last().toString()

                    }
            )
        }
        return mutableListOf;
    }

    fun getFileSize(path: String): Long {
         return if (File(path).isDirectory) {
            -1;
        } else {
            File(path).length();
        }
    }

    private fun getFileType(path: String): String {
         return if (File(path).isDirectory) {
            "directory"
        } else {
            "file"
        }
    }
}
