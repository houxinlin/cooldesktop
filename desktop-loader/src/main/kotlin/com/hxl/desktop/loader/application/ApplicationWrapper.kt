package com.hxl.desktop.loader.application

import com.desktop.application.definition.application.Application
import com.desktop.application.definition.application.webmini.WebMiniApplication
import com.hxl.cooldesktop.application.event.definition.ApplicationEventListener
import com.hxl.desktop.loader.cache.ResourceCache
import org.slf4j.LoggerFactory

abstract class ApplicationWrapper(val application: Application,
                                  val applicationEventListener: ApplicationEventListener?
) : ResourceCache() {
    companion object {
        private val log = LoggerFactory.getLogger(ApplicationWrapper::class.java)
    }
    abstract fun doLoad(path: String):ByteArray?
    fun loadResource(path: String): ByteArray? {
        if (!inCache(path)) doLoad(path)
        return getCacheResource(path)
    }

    override fun init() {
        if (application is WebMiniApplication) {
            loadResource("/index.html")
            return
        }
        loadResource("index")
    }
}