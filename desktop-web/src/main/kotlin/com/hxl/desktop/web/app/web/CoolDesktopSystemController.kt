package com.hxl.desktop.web.app.web

import com.hxl.desktop.system.sys.CoolDesktopSystem
import common.extent.asHttpResponseBody
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

    /**
     * 更改壁纸
     */
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


}