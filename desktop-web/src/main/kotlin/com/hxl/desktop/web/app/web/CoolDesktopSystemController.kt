package com.hxl.desktop.web.app.web

import com.hxl.desktop.common.model.Page
import com.hxl.desktop.common.core.Constant
import com.hxl.desktop.system.ano.NotifyWebSocket
import com.hxl.desktop.common.kotlin.extent.toFile
import com.hxl.desktop.common.model.FileHandlerResult
import com.hxl.desktop.common.utils.JSON
import com.hxl.desktop.database.CoolDesktopDatabase
import com.hxl.desktop.file.bean.FileAttribute
import com.hxl.desktop.system.ano.LogRecord
import com.hxl.desktop.system.core.manager.OpenUrlManager
import com.hxl.desktop.system.core.sys.CoolDesktopSystem
import com.hxl.desktop.system.ano.UnifiedApiResult
import com.hxl.desktop.web.util.JsonArrayConvert
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
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


    /**
    * @description: 修改壁纸
    * @date: 2022/9/2 下午11:58
    */

    @LogRecord(logName = "修改壁纸")
    @PostMapping("wallpaper/change")
    fun changeWallpaper(@RequestParam file: MultipartFile): FileHandlerResult {
        return coolDesktopSystem.changeWallpaper(file)
    }


    /**
    * @description: 获取系统配置
    * @date: 2022/9/2 下午11:56
    */

    @GetMapping("cooldesktop/config/get")
    fun getCoolDesktopConfigs(): MutableMap<String, String> {
        return coolDesktopSystem.getCoolDesktopConfigs()
    }
    @PostMapping("cooldesktop/config/set")
    fun getCoolDesktopConfigs(@RequestParam("key")key:String,@RequestParam("value")value: String):String {
        return coolDesktopSystem.setCoolDesktopConfigs(key,value)
    }

    /**
    * @description: 重置SSH公钥
    * @date: 2022/9/2 下午11:59
    */

    @LogRecord(logName = "重置SSH")
    @PostMapping("ssh/reset")
    fun configSecureShell(): String {
        return coolDesktopSystem.configSecureShell()
    }

    @LogRecord(logName = "设置SSH用户名")
    @PostMapping("ssh/username/set")
    fun configSecureShellUser(@RequestParam("userName") userName: String): String {
        return coolDesktopSystem.configSecureShellUser(userName)
    }

    @LogRecord(logName = "重置登录密码")
    @PostMapping("login/passwd/reset")
    fun resetLogoPasswd(@RequestParam("pass") pass: String): String {
        return coolDesktopSystem.resetLoginPasswd(pass)
    }

    @LogRecord(logName = "添加开放URL")
    @PostMapping("open/url/add")
    fun addOpenUrl(@RequestParam("url") url: String): String {
        return OpenUrlManager.register(url)
    }

    @GetMapping("open/url/get")
    fun getOpenUrl(): MutableList<String> {
        return OpenUrlManager.getOpenUrl()
    }

    @LogRecord(logName = "open/url/add")
    @PostMapping("removeOpenUrl")
    fun removeOpenUrl(@RequestParam("url") url: String): String {
        OpenUrlManager.unregister(url)
        return Constant.StringConstant.DELETE_SUCCESS
    }

    @LogRecord(logName = "设置APP属性")
    @PostMapping("app/property/set")
    fun setSysProperty(@RequestParam("key") key: String, @RequestParam("value") value: String): String {
        coolDesktopDatabase.setAppProperties(key, value)
        return Constant.StringConstant.OK
    }

    @GetMapping("app/property/get")
    fun getSysProperty(@RequestParam("key") key: String): Any {
        return coolDesktopDatabase.getAppProperties(key)

    }

    @LogRecord(logName = "添加桌面文件")
    @NotifyWebSocket(subject = Constant.WebSocketSubjectNameConstant.REFRESH_DESKTOP_REFRESH)
    @PostMapping("desktop/file/add")
    fun addDesktopFile(@RequestParam("path") path: String): String {
        val fileAttributes = listDesktopFile()
        fileAttributes.forEach { if (it.path == path) return Constant.StringConstant.DUPLICATE }
        val listPath = fileAttributes.stream().map { it.path }.collect(Collectors.toList())
        listPath.add(path)
        coolDesktopDatabase.setAppProperties(Constant.KeyNames.DESKTOP_FILE_KEY, listPath)
        return Constant.StringConstant.OK
    }

    @GetMapping("desktop/file/list")
    fun listDesktopFile(): List<FileAttribute> {
        val pathList = coolDesktopDatabase.getAppProperties(Constant.KeyNames.DESKTOP_FILE_KEY, "[]")
        val newPathList = JSON.parseList(pathList, String::class.java)?.filter { it.toFile().exists() }
        coolDesktopDatabase.setAppProperties(Constant.KeyNames.DESKTOP_FILE_KEY, newPathList!!)
        return JsonArrayConvert().apply(pathList)
    }

    @LogRecord(logName = "删除桌面文件")
    @NotifyWebSocket(subject = Constant.WebSocketSubjectNameConstant.REFRESH_DESKTOP_REFRESH)
    @PostMapping("desktop/file/remove")
    fun removeDesktopFile(@RequestParam("path") path: String): String {
        val fileAttributes =
            JsonArrayConvert().apply(coolDesktopDatabase.getAppProperties(Constant.KeyNames.DESKTOP_FILE_KEY))
        val newList = fileAttributes.filter { it.path != path }.stream().map { it.path }.collect(Collectors.toList())
        coolDesktopDatabase.setAppProperties(Constant.KeyNames.DESKTOP_FILE_KEY, newList)
        return Constant.StringConstant.OK
    }

    @GetMapping("system/info/base")
    fun getBaseInfo(): MutableMap<String, Any> {
        return mutableMapOf("timer" to System.currentTimeMillis(), "user" to System.getProperty("user.name"))
    }

    @GetMapping("log/list")
    fun getSystemLog(@RequestParam(required = false, value = "page", defaultValue = "1") page: Int,
                     @RequestParam("logType") logType: String,
                     @RequestParam("logLevel") logLevel: String,
                     @RequestParam("filterTimer") filterTimer: String
    ): Page<Map<String, Any>> {
        return coolDesktopDatabase.listSysLog(logType, logLevel, filterTimer, page)
    }
}