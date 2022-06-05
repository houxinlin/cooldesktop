package com.hxl.desktop.loader.application

import com.desktop.application.definition.application.Application
import com.desktop.application.definition.application.easyapp.EasyApplication
import com.desktop.application.definition.application.webmini.WebMiniApplication
import com.hxl.desktop.loader.cache.ResourceCache
import com.hxl.fm.pk.FilePackage
import org.slf4j.LoggerFactory
import java.io.BufferedInputStream
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.net.URL
import kotlin.math.log

class ApplicationWrapper(var application: Application) : ResourceCache() {
    private val log = LoggerFactory.getLogger(ApplicationWrapper::class.java)
    private fun loadByteFromWebApplication(path: String): ByteArray? {
        val webMiniApplication = application as WebMiniApplication
        val bufferedInputStream = BufferedInputStream(FileInputStream(webMiniApplication.applicationPath))
        bufferedInputStream.skip(webMiniApplication.staticResOffset)
        val fileTable = FilePackage.decode(bufferedInputStream.readBytes())
        if (fileTable?.get(path) == null) {
            return null;
        }
        addCacheResource(path, fileTable.get(path)!!)
        return getCacheResource(path)
    }

    private fun loadByteFromEasyApplication(path: String): ByteArray? {
        var applicationPath = application.applicationPath
        if (!applicationPath.startsWith("jar:file:")) {
            applicationPath = "jar:file:$applicationPath!$path"
        }
        try {
            val bytes = URL(applicationPath).openStream().readBytes()
            addCacheResource(path, bytes)
        } catch (e: IOException) {
            log.warn("加载资源异常:" + e.message)
        }
        return null
    }

    fun loadResource(path: String): ByteArray? {
        if (!inCache(path)) {
            if (application is WebMiniApplication) {
                loadByteFromWebApplication(path)
            }
            if (application is EasyApplication) {
                loadByteFromEasyApplication(path)
            }
        }
        return getCacheResource(path)
    }

    override fun init() {
        if (application is WebMiniApplication) {
            loadResource("/index.html")
            return
        }
    }
}