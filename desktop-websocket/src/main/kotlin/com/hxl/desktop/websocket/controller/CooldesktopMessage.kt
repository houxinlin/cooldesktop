package com.hxl.desktop.websocket.controller

import com.hxl.desktop.loader.application.ApplicationManager
import com.hxl.desktop.websocket.event.ClientEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller


@Controller
class CooldesktopMessage {

    @Autowired
    lateinit var applicationManager: ApplicationManager
    @MessageMapping("/event")
    fun event(message: ClientEvent) {
       if (message.data=="startApplication")  applicationManager.pushOpenApplicationEvent(message.data)
        if (message.data=="closeApplication")  applicationManager.pushCloseApplicationEvent(message.data)
    }
}