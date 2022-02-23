package com.hxl.desktop.websocket

import com.hxl.desktop.websocket.action.TerminalWebSocketConnectionAction
import com.hxl.desktop.websocket.ssh.SshManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.HtmlUtils


@RestController
class WebSocketController {

    @Autowired
    lateinit var terminalWebSocketConnectionAction: TerminalWebSocketConnectionAction

    @MessageMapping("/desktop")
    fun greeting(message: String, @Header("simpSessionId")  sessionId:String) {
        terminalWebSocketConnectionAction.onMessage(message,sessionId);
    }

}