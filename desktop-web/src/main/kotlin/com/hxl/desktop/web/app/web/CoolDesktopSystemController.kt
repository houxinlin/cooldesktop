package com.hxl.desktop.web.app.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.hxl.desktop.common.core.Constant
import com.hxl.desktop.common.core.ano.NotifyWebSocket
import com.hxl.desktop.common.extent.toFile
import com.hxl.desktop.common.result.FileHandlerResult
import com.hxl.desktop.common.utils.JSON
import com.hxl.desktop.database.CoolDesktopDatabase
import com.hxl.desktop.file.bean.FileAttribute
import com.hxl.desktop.system.manager.OpenUrlManager
import com.hxl.desktop.system.sys.CoolDesktopSystem
import com.hxl.desktop.web.config.advice.UnifiedApiResult
import com.hxl.desktop.web.util.JsonArrayConvert
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.function.Function
import java.util.stream.Collectors

/**
 * 系统相关接口定义
 */
@RestController
@UnifiedApiResult
@RequestMapping("/desktop/api/system/")
class CoolDesktopSystemController {


    @Autowired
    lateinit var coolDesktopSystem: CoolDesktopSystem

    @Autowired
    lateinit var coolDesktopDatabase: CoolDesktopDatabase

    @PostMapping("changeWallpaper")
    fun changeWallpaper(@RequestParam file: MultipartFile): FileHandlerResult {
        return coolDesktopSystem.changeWallpaper(file)
    }

    @PostMapping("getCoolDesktopConfigs")
    fun getCoolDesktopConfigs(): MutableMap<String, String> {
        return coolDesktopSystem.getCoolDesktopConfigs()
    }

    @PostMapping("configSecureShell")
    fun configSecureShell(): String {
        return coolDesktopSystem.configSecureShell()
    }

    @PostMapping("configSecureShellUser")
    fun configSecureShellUser(@RequestParam("userName") userName: String): String {
        return coolDesktopSystem.configSecureShellUser(userName)
    }

    @PostMapping("resetLoginPasswd")
    fun resetLogoPasswd(): String {
        return coolDesktopSystem.resetLoginPasswd()
    }

    @PostMapping("addOpenUrl")
    fun addOpenUrl(@RequestParam("url") url: String): String {
        return OpenUrlManager.register(url)
    }

    @PostMapping("getOpenUrl")
    fun getOpenUrl(): MutableList<String> {
        return OpenUrlManager.getOpenUrl()
    }

    @PostMapping("removeOpenUrl")
    fun removeOpenUrl(@RequestParam("url") url: String): String {
        OpenUrlManager.unregister(url)
        return Constant.StringConstant.DELETE_SUCCESS
    }


    @PostMapping("setSysProperty")
    fun setSysProperty(@RequestParam("key") key: String, @RequestParam("value") value: String): String {
        coolDesktopDatabase.setSysProperties(key, value)
        return Constant.StringConstant.OK
    }

    @PostMapping("getSysProperty")
    fun getSysProperty(@RequestParam("key") key: String): Any {
        return coolDesktopDatabase.getSysProperties(key)

    }

    @NotifyWebSocket(subject = Constant.WebSocketSubjectNameConstant.REFRESH_DESKTOP_REFRESH)
    @PostMapping("desktop/file/add")
    fun addDesktopFile(@RequestParam("path") path: String): String {
        var fileAttributes =listDesktopFile()
        fileAttributes.forEach { if (it.path == path) return Constant.StringConstant.DUPLICATE }
        val listPath = fileAttributes.stream().map { it.path }.collect(Collectors.toList())
        listPath.add(path)
        coolDesktopDatabase.setSysProperties(Constant.KeyNames.DESKTOP_FILE_KEY, listPath)
        return Constant.StringConstant.OK
    }

    @GetMapping("desktop/file/list")
    fun listDesktopFile(): List<FileAttribute> {
        val pathList = coolDesktopDatabase.getSysProperties(Constant.KeyNames.DESKTOP_FILE_KEY,"[]")
        val newPathList = JSON.parseList(pathList, String::class.java)?.filter { it.toFile().exists() }
        coolDesktopDatabase.setSysProperties(Constant.KeyNames.DESKTOP_FILE_KEY,newPathList!!)
        return JsonArrayConvert().apply(pathList)
    }

    @NotifyWebSocket(subject = Constant.WebSocketSubjectNameConstant.REFRESH_DESKTOP_REFRESH)
    @PostMapping("desktop/file/remove")
    fun removeDesktopFile(@RequestParam("path") path: String): String {
        var fileAttributes =
            JsonArrayConvert().apply(coolDesktopDatabase.getSysProperties(Constant.KeyNames.DESKTOP_FILE_KEY))
        val newList = fileAttributes.filter { it.path != path }.stream().map { it.path }.collect(Collectors.toList())
        coolDesktopDatabase.setSysProperties(Constant.KeyNames.DESKTOP_FILE_KEY, newList)
        return Constant.StringConstant.OK
    }

}