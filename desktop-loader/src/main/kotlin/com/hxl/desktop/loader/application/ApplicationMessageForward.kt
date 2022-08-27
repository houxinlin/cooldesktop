package com.hxl.desktop.loader.application

import com.desktop.application.definition.application.easyapp.EasyApplication
import com.fasterxml.jackson.databind.ObjectMapper
import com.hxl.cooldesktop.application.event.definition.ApplicationMessagePublish
import com.hxl.desktop.common.core.Constant
import com.hxl.desktop.system.core.WebSocketMessageBuilder
import com.hxl.desktop.system.core.WebSocketSender


class ApplicationMessageForward(private val application: EasyApplication) : ApplicationMessagePublish {
    lateinit var webSocketSender: WebSocketSender
    override fun push(data: MutableMap<String, String>?) {
        push(ObjectMapper().writeValueAsString(data))
    }

    override fun push(data: String?) {
        webSocketSender.send(WebSocketMessageBuilder.Builder()
            .applySubject(Constant.WebSocketSubjectNameConstant.APPLICATION_MESSAGE_PUSH)
            .addItem("applicationId",application.applicationId)
            .addItem("data", data)
            .build())
    }
}