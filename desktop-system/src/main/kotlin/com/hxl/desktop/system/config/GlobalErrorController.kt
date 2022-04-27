package com.hxl.desktop.system.config

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalErrorController {
    private val logger = LoggerFactory.getLogger(GlobalErrorController::class.java)

    @ExceptionHandler(Exception::class)
    fun handlerGlobalError(exception: java.lang.Exception): Any {
        logger.error(exception.message)
        return mutableMapOf("code" to -1, "msg" to "请求失败")
    }

}