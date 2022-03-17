package com.hxl.desktop.web


import com.alibaba.fastjson.JSON
import common.result.FileHandlerResult
import org.springframework.beans.factory.config.BeanDefinitionCustomizer
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.web.servlet.ServletComponentScan
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import java.io.File
import java.net.URLClassLoader

@SpringBootApplication(scanBasePackages = ["com.hxl.desktop"], exclude = [DataSourceAutoConfiguration::class])
@ServletComponentScan
@EnableAsync
@EnableAspectJAutoProxy
class Application

fun main(args: Array<String>) {
    var application =
        SpringApplication.run(Application::class.java, *args) as AnnotationConfigServletWebServerApplicationContext
    //移除默认的requestMappingHandlerMapping
    application.removeBeanDefinition("requestMappingHandlerMapping")
}
