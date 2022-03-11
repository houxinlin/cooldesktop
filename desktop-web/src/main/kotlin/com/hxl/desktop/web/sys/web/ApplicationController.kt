package com.hxl.desktop.web.sys.web

import org.springframework.web.bind.annotation.*

/**
 * @author:   HouXinLin
 * @email:    2606710413@qq.com
 * @date:     20212021/12/18
 * @describe: application controller
 * @version:  v1.0
 */
@RequestMapping("/desktop/api/sys/")
@RestController
class ApplicationController {
    @PostMapping("setAppProperty")
    fun setAppProperty(): Any {
        return "";
    }

    @PostMapping("getAppProperty")
    fun getAppProperty(): Any {
        return "";
    }

}