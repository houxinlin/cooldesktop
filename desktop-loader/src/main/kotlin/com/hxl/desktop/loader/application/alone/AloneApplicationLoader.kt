package com.hxl.desktop.loader.application.alone

import org.springframework.boot.web.embedded.tomcat.ConfigurableTomcatWebServerFactory
import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.stereotype.Service

/**
 * 预留
 */
@Service
class AloneApplicationLoader : WebServerFactoryCustomizer<ConfigurableTomcatWebServerFactory> {
//    @Autowired
//    lateinit var applicationRegister: ApplicationRegister
    override fun customize(factory: ConfigurableTomcatWebServerFactory) {

    }


}