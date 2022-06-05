package com.hxl.desktop.web.app.web

import com.desktop.application.definition.application.Application
import com.hxl.desktop.loader.application.ApplicationInstallDispatcher
import com.hxl.desktop.loader.application.ApplicationRegister
import com.hxl.desktop.loader.core.ApplicationDownloadManager
import com.hxl.desktop.web.config.advice.UnifiedApiResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

/**
 * 应用程序相关接口定义
 */
@UnifiedApiResult
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
    fun list():  List<Application>  {
        return applicationRegister.listApplication()
    }

    /**
     * 从中央服务器下载并且按转应用
     */
    @PostMapping("install")
    fun install(@RequestParam("id") id: String): Any {
        return applicationDownloadManager.install(id)
    }

    /**
     * 卸载应用
     */
    @PostMapping("uninstall")
    fun uninstall(@RequestParam("id") id: String): String {
        return applicationInstallDispatcher.uninstallApplicationDispatcher(id)
    }

    /**
     * 安装自定义应用
     */
    @PostMapping("installCustomApplication")
    fun installCustomApplication(@RequestParam file: MultipartFile): String {
        return applicationInstallDispatcher.installCustomApplicationDispatcher(file)
    }
}