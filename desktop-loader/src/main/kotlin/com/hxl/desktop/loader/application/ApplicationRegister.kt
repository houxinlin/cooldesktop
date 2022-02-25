package com.hxl.desktop.loader.application

import com.desktop.application.definition.application.Application
import com.desktop.application.definition.application.webmini.WebMiniApplication
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*
import java.util.stream.Collectors

@Service
class ApplicationRegister {
    var log: Logger = LoggerFactory.getLogger(ApplicationRegister::class.java)
    private var webMiniApplicationMap = mutableMapOf<String, ApplicationWrapper>()
    fun registerWebApp(webMiniApplication: ApplicationWrapper) {
        log.info("注册WebApplication{}", webMiniApplication)
        webMiniApplicationMap[webMiniApplication.application.applicationId] = webMiniApplication
    }

    fun listApplication(): MutableList<Application> {
        return webMiniApplicationMap.values.stream().map { it.application }.collect(Collectors.toList())
    }

    fun getWebMinApplication(id: String): ApplicationWrapper? {
        return webMiniApplicationMap[id]
    }
}