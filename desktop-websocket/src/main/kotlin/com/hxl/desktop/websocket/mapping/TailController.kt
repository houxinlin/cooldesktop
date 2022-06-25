package com.hxl.desktop.websocket.mapping

import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller

@Controller
class TailController {
    @MessageMapping
    fun tailMessage() {

    }
}