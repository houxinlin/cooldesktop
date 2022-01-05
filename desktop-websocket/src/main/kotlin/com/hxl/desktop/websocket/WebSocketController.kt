package com.hxl.desktop.websocket

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.HtmlUtils


@RestController
class WebSocketController {
    @Autowired
    private val simpMessagingTemplate: SimpMessagingTemplate? = null

    @MessageMapping("/desktop")
    fun greeting(message: String) {
    }

    @GetMapping("/test")
    fun test() {
        simpMessagingTemplate!!.convertAndSend("/topic/greetings", "test")
    }
}