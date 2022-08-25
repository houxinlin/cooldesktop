package com.hxl.desktop.loader.application

import com.desktop.application.definition.application.ApplicationLoader
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ApplicationLoaders {

    @Autowired
    lateinit var applicationInstallDispatcher: ApplicationInstallDispatcher

    @Autowired
    lateinit var applicationManager: ApplicationManager


    @Autowired
    fun setApplicationLoader(loads: MutableList<ApplicationLoader<*>>) {
        loads.forEach(ApplicationLoader<*>::loadApplicationFromLocal)
    }
}