package com.hxl.desktop.websocket.action

import org.springframework.web.socket.WebSocketSession

 abstract class WebSocketConnectionAction {
    abstract fun onMessage(message: String, sessionId: String)

    abstract fun action(webSocketSession: WebSocketSession)

    abstract fun closeSession(webSocketSession: WebSocketSession)

    abstract fun support(): String


}