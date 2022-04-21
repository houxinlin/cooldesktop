package com.hxl.desktop.web.app.web

import com.hxl.desktop.common.core.Constant
import com.hxl.desktop.common.core.Directory
import com.hxl.desktop.file.extent.writeStringBuffer
import com.hxl.desktop.system.sys.CoolDesktopSystem
import com.hxl.desktop.common.bean.failResponse
import com.hxl.desktop.common.extent.asHttpResponseBody
import com.hxl.desktop.common.extent.asHttpResponseBodyOfMessage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import kotlin.io.path.notExists

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

    @PostMapping("resetLogoPasswd")
    fun resetLogoPasswd(): Any {
        return coolDesktopSystem.resetLogoPasswd()
    }

    @PostMapping("addOpenUrl")
    fun addOpenUrl(@RequestParam("url") url: String): Any {
        if (url.isBlank()) {
            return failResponse(Constant.StringConstant.CANNOT_BLANK)
        }
        if (url == "/"){
            return failResponse(Constant.StringConstant.NOT_SUPPORT_PARAMETER)
        }
        val newUrl = if (url.startsWith("/")) url else "/${url}"
        val oldOpenPath = Paths.get(Directory.getOpenUrlDirectory(), Constant.FileName.OPEN_URL)
        var data = StringBuffer(newUrl).append("\r").toString()
        Files.write(oldOpenPath, data.toByteArray(), StandardOpenOption.CREATE, StandardOpenOption.APPEND)
        return oldOpenPath.asHttpResponseBody()
    }

    @PostMapping("getOpenUrl")
    fun getOpenUrl(): Any {
        val oldOpenPath = Paths.get(Directory.getOpenUrlDirectory(), Constant.FileName.OPEN_URL)
        if (oldOpenPath.notExists()) {
            return arrayOf<String>().asHttpResponseBody()
        }
        return oldOpenPath.toFile().readLines().asHttpResponseBody()
    }

    @PostMapping("removeOpenUrl")
    fun removeOpenUrl(@RequestParam("url") url: String): Any {
        val oldOpenPath = Paths.get(Directory.getOpenUrlDirectory(), Constant.FileName.OPEN_URL)
        if (oldOpenPath.notExists()) {
            return arrayOf<String>().asHttpResponseBody()
        }
        val allOpenURLs = oldOpenPath.toFile().readLines()
        var stringBuffer = StringBuffer()
        allOpenURLs.filterNot { it == url }.forEach { stringBuffer.append(it);stringBuffer.append("\r\n") }

        oldOpenPath.toFile().writeStringBuffer(stringBuffer)
        return Constant.StringConstant.DELETE_SUCCESS.asHttpResponseBodyOfMessage(0)
    }
}