package com.hxl.desktop.web


import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.web.servlet.ServletComponentScan

@SpringBootApplication(scanBasePackages =["com.hxl.desktop"])
@ServletComponentScan
class Application

fun main(args:Array<String>) {
    SpringApplication.run(Application::class.java,*args)
}
