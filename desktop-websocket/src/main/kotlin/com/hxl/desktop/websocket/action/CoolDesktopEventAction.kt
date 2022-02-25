package com.hxl.desktop.websocket.action

import com.hxl.desktop.system.core.WebSocketSender
import com.hxl.desktop.websocket.utils.WebSocketUtils
import org.springframework.stereotype.Service
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession

@Service
class CoolDesktopEventAction : WebSocketConnectionAction(), WebSocketSender {
    lateinit var coolDesktopEventSocket: WebSocketSession

    override fun onMessage(message: String, sessionId: String) {
    }

    override fun sender( msg: String,id: String) {
        coolDesktopEventSocket.sendMessage(TextMessage(WebSocketUtils.createMessage(msg.toByteArray())))
    }

    override fun action(webSocketSession: WebSocketSession) {
        coolDesktopEventSocket = webSocketSession
    }

    override fun closeSession(webSocketSession: WebSocketSession) {
    }

    override fun support(): String {
        return "/topic/events"
    }
}