package com.hxl.desktop.loader.application

import com.desktop.application.definition.application.ApplicationLoader
import com.hxl.desktop.system.core.CoolDesktopBeanRegister
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.annotation.AliasFor
import org.springframework.stereotype.Component
import java.io.File
import java.net.URL
import java.net.URLClassLoader

@Component
class ApplicationLoaders {

    @Autowired
    lateinit var applicationInstallDispatcher: ApplicationInstallDispatcher

    @Autowired
    lateinit var applicationRegister: ApplicationRegister


    @Autowired
    fun setApplicationLoader(loads: MutableList<ApplicationLoader<*>>) {
        loads.forEach(ApplicationLoader<*>::loadApplicationFromLocal)
    }
}