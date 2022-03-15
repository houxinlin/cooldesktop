package com.hxl.desktop.loader.application.alone

import com.desktop.application.definition.application.ApplicationLoader
import com.hxl.desktop.common.core.Directory
import com.hxl.desktop.loader.application.ApplicationRegister
import common.extent.toFile
import common.extent.toPath
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.embedded.tomcat.ConfigurableTomcatWebServerFactory
import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.stereotype.Service
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import javax.annotation.PostConstruct

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