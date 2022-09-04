package com.hxl.desktop.web.app.web

import com.desktop.application.definition.application.Application
import com.hxl.desktop.common.core.Constant
import com.hxl.desktop.common.kotlin.extent.toHttpResponse
import com.hxl.desktop.database.CoolDesktopDatabase
import com.hxl.desktop.database.CoolDesktopDatabaseConfigKeys
import com.hxl.desktop.loader.application.ApplicationInstallDispatcher
import com.hxl.desktop.loader.application.ApplicationManager
import com.hxl.desktop.loader.core.ApplicationDownloadManager
import com.hxl.desktop.system.ano.LogRecord
import com.hxl.desktop.system.ano.NotifyConfigMiss
import com.hxl.desktop.system.ano.UnifiedApiResult
import com.hxl.desktop.system.core.WebSocketMessageBuilder
import com.hxl.desktop.system.core.WebSocketSender
import com.hxl.desktop.web.util.http.client.HttpClientUtils
import com.hxl.desktop.web.util.http.client.ListMapResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.http.ResponseEntity
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.HandlerMapping
import java.net.URL
import javax.servlet.http.HttpServletRequest

/**
 * 应用程序相关接口定义
 */
@UnifiedApiResult
@RestController
@RequestMapping("/desktop/api/application/")
class DesktopApplicationController {
    companion object {
        const val APPLICATION_SERVER_TYPE_LIST = "software/api/types"
        const val APPLICATION_SERVER_LIST = "software/api/list"
        const val RESOURCE_PREFIX = "/desktop/api/application/server/resource/get"
    }

    @Autowired
    lateinit var applicationManager: ApplicationManager

    @Autowired
    lateinit var applicationDownloadManager: ApplicationDownloadManager

    @Autowired
    lateinit var applicationInstallDispatcher: ApplicationInstallDispatcher

    @Autowired
    lateinit var coolDesktopDatabase: CoolDesktopDatabase

    /**
     * 获取所有应用
     */
    @GetMapping("list")
    fun list(): List<Application> {
        return applicationManager.listApplication()
    }

    /**
     * 从中央服务器下载并且按转应用
     */
    @LogRecord(logName = "安装软件")
    @PostMapping("install")
    fun install(@RequestParam("id") id: String): Any {
        return applicationDownloadManager.install(id)
    }

    /**
     * 卸载应用
     */
    @LogRecord(logName = "卸载软件")
    @PostMapping("uninstall")
    fun uninstall(@RequestParam("id") id: String): String {
        return applicationInstallDispatcher.uninstallApplicationDispatcher(id)
    }

    /**
     * 安装自定义应用
     */
    @LogRecord(logName = "安装自定义软件")
    @PostMapping("custom/application/install")
    fun installCustomApplication(@RequestParam file: MultipartFile): String {
        return applicationInstallDispatcher.installCustomApplicationDispatcher(file)
    }


    /**
     * @description: 获取应用程序服务器中所有应用类型
     * @date: 2022/9/3 上午2:39
     */

    @NotifyConfigMiss(sysConfigKey="application_server_host")
    @GetMapping("server/type/list")
    fun listServerApplicationTypes(): List<Map<String, Any>> {
        var host = coolDesktopDatabase.getSysConfig(CoolDesktopDatabaseConfigKeys.APPLICATION_SERVER_HOST.keyName)
        if (!StringUtils.hasText(host) ) return mutableListOf()
        if (!host.endsWith("/")) host = "$host/"
        host = "$host$APPLICATION_SERVER_TYPE_LIST"
        return ListMapResponse(HttpClientUtils.createGetRequest(host)).getValue()
    }

    /**
     * 根据类型获取所有应用
     */
    @NotifyConfigMiss(sysConfigKey="application_server_host")
    @GetMapping("server/app/list")
    fun listServerApplicationByType(@RequestParam("typeName") typeName: String): List<Map<String, Any>> {
        var host = coolDesktopDatabase.getSysConfig(CoolDesktopDatabaseConfigKeys.APPLICATION_SERVER_HOST.keyName)
        if (!StringUtils.hasText(host) ) return mutableListOf()
        if (!host.endsWith("/")) host = "$host/"
        host = "$host$APPLICATION_SERVER_LIST"
        return ListMapResponse(HttpClientUtils.createGetRequest(host, mutableMapOf("typeName" to typeName))).getValue()
    }

    /**
     * 图片路径中转
     */
    @NotifyConfigMiss(sysConfigKey="application_server_host")
    @GetMapping("server/resource/get/{path}/**")
    fun serverResource(@PathVariable("path") path: String, request: HttpServletRequest): ResponseEntity<Resource> {
        val host = coolDesktopDatabase.getSysConfig(CoolDesktopDatabaseConfigKeys.APPLICATION_SERVER_HOST.keyName)
        if (!StringUtils.hasText(host) ) return ResponseEntity.notFound().build()
        val fullUrl = request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE) as String
        val pathOfApplication = fullUrl.removePrefix(RESOURCE_PREFIX)
        return URL("$host$pathOfApplication").openConnection().getInputStream().readBytes().toHttpResponse()
    }
}