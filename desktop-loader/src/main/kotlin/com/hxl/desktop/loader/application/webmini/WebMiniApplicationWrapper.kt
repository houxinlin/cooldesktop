package com.hxl.desktop.loader.application.webmini

import com.desktop.application.definition.application.Application
import com.desktop.application.definition.application.webmini.WebMiniApplication
import com.hxl.desktop.loader.application.ApplicationWrapper
import com.hxl.fm.pk.FilePackage
import java.io.BufferedInputStream
import java.io.FileInputStream

class WebMiniApplicationWrapper(application: Application) :ApplicationWrapper(application, null){
    override fun doLoad(path: String): ByteArray? {
        return  loadByteDataFromWebApplication(path);
    }
    private fun loadByteDataFromWebApplication(path: String): ByteArray? {
        val webMiniApplication = application as WebMiniApplication
        val bufferedInputStream = BufferedInputStream(FileInputStream(webMiniApplication.applicationPath))
        bufferedInputStream.skip(webMiniApplication.staticResOffset)
        val fileTable = FilePackage.decode(bufferedInputStream.readBytes())
        if (fileTable?.get(path) == null) return null
        addCacheResource(path, fileTable.get(path)!!)
        return getCacheResource(path)
    }

}