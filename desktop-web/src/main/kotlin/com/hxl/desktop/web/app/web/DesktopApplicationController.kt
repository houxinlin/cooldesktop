package com.hxl.desktop.web.app.web

import common.extent.asHttpResponseBody
import com.hxl.desktop.loader.application.ApplicationRegister
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/desktop/api/application/")
class DesktopApplicationController {
    @Autowired
    lateinit var applicationRegister: ApplicationRegister

    @GetMapping("list")
    fun list(): Any {
        return applicationRegister.listApplication().asHttpResponseBody()
    }
}