package com.hxl.desktop.websocket.config

import com.hxl.desktop.websocket.utils.StompPrincipal
import org.springframework.context.annotation.Configuration
import org.springframework.http.server.ServerHttpRequest
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer
import org.springframework.web.socket.server.support.DefaultHandshakeHandler
import java.security.Principal
import java.util.*


@Configuration
@EnableWebSocketMessageBroker
class DesktopWebSocketConfigurer : WebSocketMessageBrokerConfigurer {
    override fun configureMessageBroker(config: MessageBrokerRegistry) {
        config.enableSimpleBroker("/desktop-topic")
        //客户端消息发送前缀
        config.setApplicationDestinationPrefixes("/desktop")
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/desktop-socket-endpoint")
            .setHandshakeHandler(object : DefaultHandshakeHandler() {
                override fun determineUser(
                    request: ServerHttpRequest,
                    wsHandler: WebSocketHandler,
                    attributes: Map<String, Any>
                ): Principal {
                    return StompPrincipal(UUID.randomUUID().toString())
                }
            })
            .setAllowedOrigins("http://192.168.0.110:3000", "http://localhost:3000")//开发的时候
            .withSockJS()
    }
}