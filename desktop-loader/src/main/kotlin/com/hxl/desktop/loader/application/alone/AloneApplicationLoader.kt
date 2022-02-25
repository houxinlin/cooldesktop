package com.hxl.desktop.loader.application.alone

import com.desktop.application.definition.application.ApplicationLoader
import com.hxl.desktop.loader.application.ApplicationRegister
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class AloneApplicationLoader: ApplicationLoader {
    @Autowired
    lateinit var applicationRegister: ApplicationRegister

    @PostConstruct
    override fun loadApplication() {

    }

}