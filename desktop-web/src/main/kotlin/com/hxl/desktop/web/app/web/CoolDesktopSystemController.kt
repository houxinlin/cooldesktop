package com.hxl.desktop.web.app.web

import com.hxl.desktop.common.core.Constant
import com.hxl.desktop.common.extent.asHttpResponseBody
import com.hxl.desktop.common.extent.asHttpResponseBodyOfMessage
import com.hxl.desktop.common.result.FileHandlerResult
import com.hxl.desktop.system.manager.OpenUrlManager
import com.hxl.desktop.system.sys.CoolDesktopSystem
import com.hxl.desktop.web.config.advice.UnifiedApiResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

/**
 * 系统相关接口定义
 */
@RestController
@UnifiedApiResult
@RequestMapping("/desktop/api/system/")
class CoolDesktopSystemController {
    @Autowired
    lateinit var coolDesktopSystem: CoolDesktopSystem

    @PostMapping("changeWallpaper")
    fun changeWallpaper(@RequestParam file: MultipartFile): FileHandlerResult {
        return coolDesktopSystem.changeWallpaper(file)
    }

    @PostMapping("getCoolDesktopProperty")
    fun getCoolDesktopProperty(): MutableMap<String, String> {
        return coolDesktopSystem.getCoolDesktopProperty()
    }

    @PostMapping("configSecureShell")
    fun configSecureShell(): String {
        return coolDesktopSystem.configSecureShell()
    }

    @PostMapping("configSecureShellUser")
    fun configSecureShellUser(@RequestParam("userName") userName: String): String {
        return coolDesktopSystem.configSecureShellUser(userName)
    }

    @PostMapping("resetLogoPasswd")
    fun resetLogoPasswd(): String {
        return coolDesktopSystem.resetLogoPasswd()
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
}