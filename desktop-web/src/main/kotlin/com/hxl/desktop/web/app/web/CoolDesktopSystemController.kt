package com.hxl.desktop.web.app.web

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/desktop/api/system/")
class CoolDesktopSystemController {
    /**
     * 更改壁纸
     */
    @PostMapping("changeWallpaper")
    fun changeWallpaper(@RequestParam file: MultipartFile): Any {
        return "OK"
    }
}