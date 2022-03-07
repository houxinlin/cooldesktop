package com.hxl.desktop.loader.application

import com.desktop.application.definition.application.Application
import com.desktop.application.definition.application.webmini.WebMiniApplication
import com.hxl.desktop.loader.cache.ResourceCache
import com.hxl.fm.pk.FilePackage
import java.io.BufferedInputStream
import java.io.FileInputStream

class ApplicationWrapper(var application: Application) : ResourceCache() {
    fun loadResource(path: String): ByteArray? {
        if (!inCache(path)) {
            if (application is WebMiniApplication) {
                var webMiniApplication = application as WebMiniApplication
                var bufferedInputStream = BufferedInputStream(FileInputStream(webMiniApplication.applicationPath))
                bufferedInputStream.skip(webMiniApplication.staticResOffset)
                var fileTable = FilePackage.decode(bufferedInputStream.readBytes())
                if (fileTable?.get(path) == null) {
                    return null;
                }
                addCacheResource(path, fileTable.get(path)!!)
            }
        }
        return getCacheResource(path)
    }

    override fun init() {
        loadResource("/index.html")
    }
}