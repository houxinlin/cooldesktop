package com.hxl.desktop.websocket.action

import org.springframework.web.socket.WebSocketSession

abstract class AbstractBasicSocketAction {
    /**
     * 处理消息
     */
    abstract fun onMessage(message: String, sessionId: String)

    /**
     * 处理新的连接
     */
    abstract fun action(subject: String,webSocketSession: WebSocketSession?)

    /**
     * 关闭连接
     */
    abstract fun closeSession(webSocketSession: WebSocketSession)

}