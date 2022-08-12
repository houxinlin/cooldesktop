package com.hxl.desktop.websocket

import com.hxl.desktop.websocket.action.TerminalWebSocketConnectionAction
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.WebSocketMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry
import org.springframework.web.socket.handler.TextWebSocketHandler

@Configuration
@EnableWebSocket
class XterminalWebSocketConfigurer : WebSocketConfigurer {
    @Autowired
    lateinit var terminalWebSocketConnectionAction: TerminalWebSocketConnectionAction
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(object : TextWebSocketHandler() {
            override fun afterConnectionEstablished(session: WebSocketSession) {
                super.afterConnectionEstablished(session)
                terminalWebSocketConnectionAction.action("", session)
            }

            override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
                super.afterConnectionClosed(session, status)
                terminalWebSocketConnectionAction.closeSession(session)
            }

            override fun handleMessage(session: WebSocketSession, message: WebSocketMessage<*>) {
                terminalWebSocketConnectionAction.onMessage(message.payload as String, session.id)
            }
        }, "/ws/websocket/terminal").setAllowedOrigins("*")
    }
}