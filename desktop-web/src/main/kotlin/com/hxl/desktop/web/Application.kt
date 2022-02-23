package com.hxl.desktop.web


import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.web.servlet.ServletComponentScan
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication(scanBasePackages =["com.hxl.desktop"])
@ServletComponentScan
@EnableAsync
class Application

fun main(args:Array<String>) {
    var run = SpringApplication.run(Application::class.java, *args)

}
