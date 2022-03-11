package com.hxl.desktop.common.core

import common.extent.toPath
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.createDirectories
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
     * 工作目录
     */
    private const val WEP_APP_STORAGE_DIRECTORY = "app/webapp"
    private const val WAR_APP_STORAGE_DIRECTORY = "app/war"
    private const val EASY_APP_STORAGE_DIRECTORY = "app/easy"
    private const val TOMCAT_BASE_WORK_DIRECTORY = "app/tomcat_base_dir"
    private const val CHUNK_DIRECTORY = "fileupload/chunk"
    private const val DATABASE_DIRECTORY = "database"
    private const val WALLPAPER_WORK_DIRECTORY = "config/wallpaper"
    private const val SSH_CONFIG_DIRECTORY = "config/ssh"
    private val WORK_DIRECTORY = arrayOf(
        CHUNK_DIRECTORY,
        DATABASE_DIRECTORY,
        WEP_APP_STORAGE_DIRECTORY,
        WAR_APP_STORAGE_DIRECTORY,
        TOMCAT_BASE_WORK_DIRECTORY,
        WALLPAPER_WORK_DIRECTORY,
        SSH_CONFIG_DIRECTORY,
        EASY_APP_STORAGE_DIRECTORY
    )


    private fun getFileSize(path: String): Long {
        if (File(path).isDirectory) {
            return -1;
        }
        return Files.size(path.toPath())
    }


    @Synchronized
    private fun initializationWorkDirectoryAndGetRoot(): String {
        var path = Paths.get(System.getProperty("user.dir"), "work")
        if (!path.exists()) {
            path.createDirectories()
        }
        //创建子工作目录
        createDirector(path.toString(), *WORK_DIRECTORY)
        return path.toString()
    }
    fun getEasyAppStorageDirectory(): String {
        return Paths.get(initializationWorkDirectoryAndGetRoot(), EASY_APP_STORAGE_DIRECTORY).toString();
    }

    fun getWebAppDirectory(): String {
        return Paths.get(initializationWorkDirectoryAndGetRoot(), WEP_APP_STORAGE_DIRECTORY).toString();
    }

    fun getWarAppDirectory(): String {
        return Paths.get(initializationWorkDirectoryAndGetRoot(), WAR_APP_STORAGE_DIRECTORY).toString();
    }

    fun getSecureShellConfigDirectory(): String {
        return Paths.get(initializationWorkDirectoryAndGetRoot(), SSH_CONFIG_DIRECTORY).toString();
    }

    fun getDatabaseDirectory(): String {
        return Paths.get(initializationWorkDirectoryAndGetRoot(), DATABASE_DIRECTORY).toString();
    }

    fun getTomcatBaseDirDirectory(): String {
        return Paths.get(initializationWorkDirectoryAndGetRoot(), TOMCAT_BASE_WORK_DIRECTORY).toString();
    }

    fun getWallpaperWorkDirectory(): String {
        return Paths.get(initializationWorkDirectoryAndGetRoot(), WALLPAPER_WORK_DIRECTORY).toString();
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
        child.toList().stream().forEach {
            var createPath = Paths.get(root, it)
            if (!createPath.exists())
                createPath.createDirectories()

        }
    }

    fun exists(path: String): Boolean {
        return Paths.get(path).exists()
    }

    fun exists(path: Path): Boolean {
        return exists(path.toString())
    }
}
