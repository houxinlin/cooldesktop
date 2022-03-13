package com.hxl.desktop.loader.application

import com.desktop.application.definition.application.Application
import com.desktop.application.definition.application.easyapp.EasyApplication
import com.desktop.application.definition.application.webmini.WebMiniApplication
import com.hxl.desktop.loader.cache.ResourceCache
import com.hxl.fm.pk.FilePackage
import java.io.BufferedInputStream
import java.io.FileInputStream
import java.net.URL

class ApplicationWrapper(var application: Application) : ResourceCache() {
    private fun loadByteFromWebApplication(path: String): ByteArray? {
        var webMiniApplication = application as WebMiniApplication
        var bufferedInputStream = BufferedInputStream(FileInputStream(webMiniApplication.applicationPath))
        bufferedInputStream.skip(webMiniApplication.staticResOffset)
        var fileTable = FilePackage.decode(bufferedInputStream.readBytes())
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
        var bytes = URL(applicationPath).openStream().readBytes()
        addCacheResource(path, bytes)
        return bytes
    }

    fun loadResource(path: String): ByteArray? {
        if (!inCache(path)) {
            if (application is WebMiniApplication) {
                return loadByteFromWebApplication(path)
            }
            if (application is EasyApplication) {
                return loadByteFromEasyApplication(path)
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