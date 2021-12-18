package com.hxl.desktop.web.response

/**
 * @author:   HouXinLin
 * @email:    2606710413@qq.com
 * @date:     20212021/12/18
 * @describe: response status
 * @version:  v1.0
 */
enum class HttpResponseStatus(var status: Int, var msg: String) {
    SUCCESS(0, "OK"),
    FAIL(-1, "无")
}