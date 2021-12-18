package com.hxl.desktop.web.config

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
class WebConfig  : WebMvcConfigurer {
    init {
        println("a")
    }
    @Bean
    fun webServerFactoryCustomizer(): WebServerFactoryCustomizer<*>? {
        return WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> { factory ->
            val error404Page = ErrorPage(HttpStatus.NOT_FOUND, "/index.html")
            factory.addErrorPages(error404Page)
        }
    }
}