package com.hxl.desktop.system.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

@Configuration
@PropertySource(value = ["classpath:/cool.properties"])
class CoolProperties {
    @Value("\${cool.software.server}")
    var softwareServer: String? = null

    @Value("\${cool.software.download}")
    var softwareDownloadUrl: String? = null
}