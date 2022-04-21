package com.hxl.desktop.web.app.web

import com.hxl.desktop.common.extent.asHttpResponseBody
import com.hxl.desktop.loader.application.ApplicationRegister
import com.hxl.desktop.loader.application.ApplicationInstallDispatcher
import com.hxl.desktop.loader.core.ApplicationDownloadManager
import com.hxl.desktop.common.extent.asHttpResponseBodyOfMessage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/desktop/api/application/")
class DesktopApplicationController {
    @Autowired
    lateinit var applicationRegister: ApplicationRegister

    @Autowired
    lateinit var applicationDownloadManager: ApplicationDownloadManager

    @Autowired
    lateinit var applicationInstallDispatcher: ApplicationInstallDispatcher

    /**
     * 获取所有应用
     */
    @GetMapping("list")
    fun list(): Any {
        return applicationRegister.listApplication().asHttpResponseBody()
    }

    /**
     * 从中央服务器下载应用
     */
    @PostMapping("install")
    fun install(@RequestParam("id") id: String): Any {
        return applicationDownloadManager.download(id)
    }

    /**
     * 卸载应用
     */
    @PostMapping("uninstall")
    fun uninstall(@RequestParam("id") id: String): Any {
        return applicationInstallDispatcher.uninstallApplicationDispatcher(id).asHttpResponseBodyOfMessage(0)
    }

    @PostMapping("installCustomApplication")
    fun installCustomApplication(@RequestParam file: MultipartFile): Any {
        return applicationInstallDispatcher.installCustomApplicationDispatcher(file).asHttpResponseBodyOfMessage(0)
    }
}