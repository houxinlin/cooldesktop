package com.hxl.desktop.loader.application

import com.desktop.application.definition.application.easyapp.EasyApplication
import com.hxl.cooldesktop.application.event.definition.ApplicationMessagePublish
import com.hxl.desktop.system.core.WebSocketSender


class ApplicationMessageForward(private val application:EasyApplication) : ApplicationMessagePublish {
    lateinit var webSocketSender: WebSocketSender
    override fun push(data: MutableMap<String, String>?) {

    }

    override fun push(data: String?) {
        println(data)
    }
}