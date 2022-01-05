package com.hxl.desktop.websocket

import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import org.springframework.http.server.ServerHttpRequest
import org.springframework.messaging.converter.MessageConverter
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration
import org.springframework.web.socket.handler.WebSocketHandlerDecorator
import org.springframework.web.socket.messaging.SessionSubscribeEvent
import org.springframework.web.socket.server.support.DefaultHandshakeHandler
import java.security.Principal
import java.util.*


@Configuration
@EnableWebSocketMessageBroker
class DesktopWebSocketConfigurer : WebSocketMessageBrokerConfigurer {
    override fun configureMessageBroker(config: MessageBrokerRegistry) {
        config.enableSimpleBroker("/desktop-topic");
        config.setApplicationDestinationPrefixes("/desktop");
    }


    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/desktop-socket-endpoint")
            .setHandshakeHandler(object : DefaultHandshakeHandler() {
                override fun determineUser(
                    request: ServerHttpRequest,
                    wsHandler: WebSocketHandler,
                    attributes: Map<String, Any>
                ): Principal? {
                    return StompPrincipal(UUID.randomUUID().toString())
                }
            })
            .setAllowedOrigins("http://localhost:3000")
            .withSockJS()
    }

    @EventListener
    fun websocketSubscribeEvent(sub: SessionSubscribeEvent) {
        println(sub.user)
        println("订阅" + sub)
    }
}