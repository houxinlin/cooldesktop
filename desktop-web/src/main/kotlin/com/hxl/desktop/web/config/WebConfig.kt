package com.hxl.desktop.web.config

import com.hxl.desktop.common.core.Directory
import com.hxl.desktop.common.kotlin.extent.toFile
import com.hxl.desktop.file.extent.writeStringBuffer
import org.springframework.boot.web.server.ErrorPage
import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * @author:   HouXinLin
 * @email:    2606710413@qq.com
 * @date:     20212021/12/18
 * @describe:
 * @version:  v1.0
 */
@Configuration
class WebConfig  : WebMvcConfigurer,WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {
    companion object{
        const val SERVER_DEFAULT_PORT=2556
    }
    override fun customize(factory: ConfigurableServletWebServerFactory) {
        val port =Directory.getPortConfigPath().toFile()
        if (!port.exists()) port.writeStringBuffer(SERVER_DEFAULT_PORT.toString())
        val p =port.readText().trim()
        factory.setPort(p.toInt())
        val error404Page = ErrorPage(HttpStatus.NOT_FOUND, "/index.html")
        factory.addErrorPages(error404Page)
    }
}