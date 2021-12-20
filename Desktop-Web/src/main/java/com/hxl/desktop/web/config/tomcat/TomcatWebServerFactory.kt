package com.hxl.desktop.web.config.tomcat

import org.apache.tomcat.util.http.LegacyCookieProcessor
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory
import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.context.annotation.Configuration


/**
 * @author:   HouXinLin
 * @email:    2606710413@qq.com
 * @date:     20212021/12/19
 * @describe:
 * @version:  v1.0xx
 */
@Configuration
class TomcatWebServerFactory : WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

    override fun customize(factory: TomcatServletWebServerFactory) {
        factory.addContextCustomizers(TomcatContextCustomizer {
            println(it.servletContext.contextPath)
        }
        )
    }
}