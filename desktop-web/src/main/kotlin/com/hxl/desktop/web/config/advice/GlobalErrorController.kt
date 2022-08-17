package com.hxl.desktop.web.config.advice

import com.hxl.desktop.common.core.Constant
import com.hxl.desktop.common.core.log.LogInfos
import com.hxl.desktop.common.core.log.LogInfosTemplate
import com.hxl.desktop.common.core.log.SystemLogRecord
import com.hxl.desktop.common.core.log.enums.CoolDesktopLogInfoType
import com.hxl.desktop.common.core.log.enums.CoolDesktopLogType
import com.hxl.desktop.system.core.WebSocketMessageBuilder
import com.hxl.desktop.system.core.WebSocketSender
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalErrorController {
    @Autowired
    private lateinit var webSocketSender: WebSocketSender

    @Autowired
    private lateinit var logRecored: SystemLogRecord

    @ExceptionHandler(Exception::class)
    fun handlerGlobalError(exception: java.lang.Exception): Any {
        exception.printStackTrace()
        val message = if (exception.message != null) exception.message else "未知错误"

        logRecored.addLog(LogInfosTemplate.ApiErrorLog("接口异常",message!!))

        webSocketSender.send(WebSocketMessageBuilder.Builder()
                .applySubject(Constant.WebSocketSubjectNameConstant.NOTIFY_MESSAGE_ERROR)
                .addItem("data", "捕获到全局异常，$message")
                .build()
        )
        return mutableMapOf("code" to -1, "msg" to "请求失败")
    }

}