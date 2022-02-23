package com.hxl.desktop.file.utils

import common.extent.toPath
import java.io.File
import java.nio.file.*
import kotlin.io.path.*

/**
 * @author:   HouXinLin
 * @email:    2606710413@163.com
 * @date:     20212021/12/18
 * @describe: Director utils
 * @version:  v1.0
 */
object Directory {
    val WORK_DIRECTOR = arrayOf("chunk", "database", "app/webapp")


    private fun getFileSize(path: String): Long {
        if (File(path).isDirectory) {
            return -1;
        }
        return Files.size(path.toPath())
    }


    fun initWorkEnvironmentDirectory(): String {
        var file = Paths.get(System.getProperty("user.dir"), "work").toString()
        if (!Paths.get(file).exists()) {
            Paths.get(file).createDirectories()
        }
        createDirector(file, *WORK_DIRECTOR)
        return file
    }

    fun getWebAppDirectory(): String {
        return Paths.get(initWorkEnvironmentDirectory(), "app/webapp").toString();
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
}
