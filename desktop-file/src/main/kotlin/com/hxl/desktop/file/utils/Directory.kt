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
    /**
     * 工作目录
     */
    private const val WEP_APP_WORK_DIRECTORY = "app/webapp"
    private const val CHUNK_DIRECTORY = "fileupload/chunk"
    private const val DATABASE_DIRECTORY = "database"
    private val WORK_DIRECTORY = arrayOf(CHUNK_DIRECTORY, DATABASE_DIRECTORY, WEP_APP_WORK_DIRECTORY)


    private fun getFileSize(path: String): Long {
        if (File(path).isDirectory) {
            return -1;
        }
        return Files.size(path.toPath())
    }


    private fun initializationWorkDirectoryAndGetRoot(): String {
        var path = Paths.get(System.getProperty("user.dir"), "work")
        if (!path.exists()) {
            path.createDirectories()
        }
        createDirector(path.toString(), *WORK_DIRECTORY)
        return path.toString()
    }

    fun getWebAppDirectory(): String {
        return Paths.get(initializationWorkDirectoryAndGetRoot(), WEP_APP_WORK_DIRECTORY).toString();
    }

    fun getChunkDirectory(): String {
        return Paths.get(initializationWorkDirectoryAndGetRoot(), CHUNK_DIRECTORY).toString();
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
