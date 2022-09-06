package com.hxl.desktop.loader.application.easyapp

import com.desktop.application.definition.application.Application
import com.hxl.cooldesktop.application.event.definition.ApplicationEventListener
import com.hxl.desktop.loader.application.ApplicationWrapper
import org.slf4j.LoggerFactory
import java.io.IOException
import java.net.URL

class EaseApplicationWrapper(application: Application,
                             applicationEventListener: ApplicationEventListener?) : ApplicationWrapper(application, applicationEventListener) {
    companion object {
        private val log = LoggerFactory.getLogger(EaseApplicationWrapper::class.java)
    }

    override fun doLoad(path: String): ByteArray? {
        return loadByteFromDataEasyApplication(path)
    }

    private fun loadByteFromDataEasyApplication(path: String): ByteArray? {
        var applicationPath = application.applicationPath
        if (!applicationPath.startsWith("jar:file:")) applicationPath = "jar:file:$applicationPath!$path"
        try {
            val bytes = URL(applicationPath).openStream().readBytes()
            addCacheResource(path, bytes)
        } catch (e: IOException) {
            log.warn("加载资源异常:" + e.message)
        }
        return null
    }

}