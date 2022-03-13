package com.hxl.desktop.system.core

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.net.URL
import java.net.URLClassLoader
import javax.annotation.PostConstruct

@Component
class Test {
    @Autowired
    lateinit var coolDesktopBeanRegister: CoolDesktopBeanRegister

    @PostConstruct
    fun test() {

    }
}