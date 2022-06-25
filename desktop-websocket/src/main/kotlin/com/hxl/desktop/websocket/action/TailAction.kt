package com.hxl.desktop.websocket.action

import org.springframework.stereotype.Service
import org.springframework.web.socket.WebSocketSession

@Service
class TailAction : WebSocketConnectionAction() {
    override fun onMessage(message: String, sessionId: String) {

    }

    override fun action(subject: String, webSocketSession: WebSocketSession) {
        println(subject)
    }

    override fun closeSession(webSocketSession: WebSocketSession) {
    }

    override fun support(subject: String): Boolean {
        return subject.startsWith("/topic/tail")
    }
}