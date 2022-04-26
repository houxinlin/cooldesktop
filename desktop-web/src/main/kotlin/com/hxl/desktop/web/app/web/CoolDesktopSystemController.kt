package com.hxl.desktop.web.app.web

import com.hxl.desktop.common.core.Constant
import com.hxl.desktop.common.extent.asHttpResponseBody
import com.hxl.desktop.common.extent.asHttpResponseBodyOfMessage
import com.hxl.desktop.system.manager.OpenUrlManager
import com.hxl.desktop.system.sys.CoolDesktopSystem
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/desktop/api/system/")
class CoolDesktopSystemController {
    @Autowired
    lateinit var coolDesktopSystem: CoolDesktopSystem

    @PostMapping("changeWallpaper")
    fun changeWallpaper(@RequestParam file: MultipartFile): Any {
        return coolDesktopSystem.changeWallpaper(file).asHttpResponseBody()

    }

    @PostMapping("getCoolDesktopProperty")
    fun getCoolDesktopProperty(): Any {
        return coolDesktopSystem.getCoolDesktopProperty().asHttpResponseBody()
    }

    @PostMapping("configSecureShell")
    fun configSecureShell(): Any {
        return coolDesktopSystem.configSecureShell().asHttpResponseBody()
    }

    @PostMapping("configSecureShellUser")
    fun configSecureShellUser(@RequestParam("userName") userName: String): Any {
        return coolDesktopSystem.configSecureShellUser(userName).asHttpResponseBody()
    }

    @PostMapping("resetLogoPasswd")
    fun resetLogoPasswd(): Any {
        return coolDesktopSystem.resetLogoPasswd()
    }

    @PostMapping("addOpenUrl")
    fun addOpenUrl(@RequestParam("url") url: String): Any {
        return OpenUrlManager.register(url).asHttpResponseBody()
    }

    @PostMapping("getOpenUrl")
    fun getOpenUrl(): Any {
        return OpenUrlManager.getOpenUrl().asHttpResponseBody()
    }

    @PostMapping("removeOpenUrl")
    fun removeOpenUrl(@RequestParam("url") url: String): Any {
        OpenUrlManager.unregister(url)
        return Constant.StringConstant.DELETE_SUCCESS.asHttpResponseBodyOfMessage(0)
    }
}