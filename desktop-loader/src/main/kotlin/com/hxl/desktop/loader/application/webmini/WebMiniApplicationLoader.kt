package com.hxl.desktop.loader.application.webmini

import com.hxl.desktop.loader.application.Application
import com.hxl.desktop.loader.application.ApplicationLoader
import com.hxl.desktop.loader.application.ApplicationRegister
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class WebMiniApplicationLoader : ApplicationLoader {
    var log: Logger = LoggerFactory.getLogger(WebMiniApplication::class.java)

    @Autowired
    lateinit var applicationRegister: ApplicationRegister

    @PostConstruct
    override fun loadApplication() {

    }
}