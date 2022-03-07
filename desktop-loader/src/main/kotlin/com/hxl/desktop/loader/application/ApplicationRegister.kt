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

    @Synchronized
    fun registerWebApp(webMiniApplication: ApplicationWrapper) {
        if (isLoaded(webMiniApplication.application.applicationId)) {
            return
        }
        log.info("注册WebApplication{}", webMiniApplication)
        webMiniApplication.init()
        webMiniApplicationMap[webMiniApplication.application.applicationId] = webMiniApplication
    }

    fun isLoaded(id: String): Boolean {
        return webMiniApplicationMap.containsKey(id)
    }

    fun listApplication(): MutableList<Application> {
        return webMiniApplicationMap.values.stream().map { it.application }.collect(Collectors.toList())
    }

    fun getWebMinApplication(id: String): ApplicationWrapper? {
        return webMiniApplicationMap[id]
    }
}