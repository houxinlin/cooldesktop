package com.hxl.desktop.file.utils

import com.hxl.desktop.common.bean.FileAttribute
import com.hxl.desktop.file.emun.FileType
import org.springframework.core.io.ClassPathResource
import java.io.File
import java.io.IOException
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.util.stream.Collectors
import kotlin.io.path.*

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
        var path = Paths.get(root)
        if (!path.exists()) return emptyList()
        if (!path.isDirectory()) return emptyList()
        return Files.list(path).collect(Collectors.toList())
    }

    private fun getFileSize(path: String): Long {
        return if (File(path).isDirectory) {
            -1;
        } else {
            File(path).length();
        }
    }

    fun getFileAttribute(path: Path): FileAttribute {
        return FileAttribute(
                path.toString(),
                getFileType(path.toString()),
                getFileSize(path.toString()),
                path.last().toString(),
                getRawFileType(path.toString()),
        )
    }

    fun initWorkEnvironmentDirectory(): String {
        var file = ClassPathResource("/work").path
        if (!Paths.get(file).exists()) {
            Paths.get(file).createDirectory()
        }
        createDirector(file, "chunk", "database", "app")
        return file
    }

    fun getChunkDirectory(): String {
        return Paths.get(initWorkEnvironmentDirectory(), "chunk").toString();
    }

    fun createChunkDirector(name: String): String {
        if (exists(Paths.get(getChunkDirectory(), name))) {
            return Paths.get(getChunkDirectory(), name).toString()
        }
        createDirector(getChunkDirectory(), name);
        return Paths.get(getChunkDirectory(), name).toString();
    }

    fun createDirector(root: String, vararg child: String) {
        child.toList().stream().forEach { Paths.get(root, it).createDirectories() }
    }

    fun exists(path: String): Boolean {
        return Paths.get(path).exists()
    }

    fun exists(path: Path): Boolean {
        return exists(path.toString())
    }

    private fun getRawFileType(path: String): String {
        var suffix = path.substring(path.lastIndexOf(".") + 1)
        if (suffix.isEmpty()) {
            return "";
        }
        return suffix;
    }

    private fun getFileType(path: String): String {
        return if (File(path).isDirectory) {
            FileType.FOLDER.typeName
        } else {
            var suffix = path.substring(path.lastIndexOf(".") + 1)
            if (suffix.isEmpty()) {
                return FileType.FILE.typeName
            }
            if (FileTypeRegister.IMAGE.contains(suffix.lowercase())) {
                return FileType.IMAGE.typeName
            }
            return suffix;
        }
    }


}
