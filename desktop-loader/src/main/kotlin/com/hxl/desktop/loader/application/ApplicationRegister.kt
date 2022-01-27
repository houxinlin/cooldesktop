package com.hxl.desktop.loader.application

import com.hxl.desktop.loader.application.webmini.WebMiniApplication
import com.hxl.fm.pk.FilePackage.readInt
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.BufferedInputStream
import java.io.FileInputStream
import java.nio.ByteBuffer

@Service
class ApplicationRegister {
    var log :Logger=LoggerFactory.getLogger(ApplicationRegister::class.java)
    private var webMiniApplicationMap = mutableMapOf<String, WebMiniApplication>()
    fun registerWebApp(webMiniApplication: WebMiniApplication) {
        log.info("注册WebApplication{}",webMiniApplication)
        webMiniApplicationMap[webMiniApplication.applicationId] = webMiniApplication
    }

    fun listApplication(): MutableList<WebMiniApplication> {
        return webMiniApplicationMap.values.toMutableList()
    }

    fun getWebMinApplication(id: String): Application? {
        return webMiniApplicationMap[id]
    }
}