package com.hxl.desktop.web


import com.alibaba.fastjson.JSON
import common.result.FileHandlerResult
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.web.servlet.ServletComponentScan
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping

@SpringBootApplication(scanBasePackages = ["com.hxl.desktop"], exclude = [DataSourceAutoConfiguration::class])
@ServletComponentScan
@EnableAsync
@EnableAspectJAutoProxy
class Application

fun main(args: Array<String>) {
    var run = SpringApplication.run(Application::class.java, *args)

}
