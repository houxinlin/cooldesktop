package com.hxl.desktop.web.app.web

import common.extent.asHttpResponseBody
import com.hxl.desktop.loader.application.ApplicationRegister
import com.hxl.desktop.loader.core.SoftwareDownloadManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/desktop/api/application/")
class DesktopApplicationController {
    @Autowired
    lateinit var applicationRegister: ApplicationRegister

    @Autowired
    lateinit var softwareDownloadManager: SoftwareDownloadManager

    /**
     * 获取所有应用
     */
    @GetMapping("list")
    fun list(): Any {
        return applicationRegister.listApplication().asHttpResponseBody()
    }

    /**
     * 安装应用
     */
    @PostMapping("install")
    fun install(@RequestParam("id") id: String): Any {
        return  softwareDownloadManager.download(id)
    }
}