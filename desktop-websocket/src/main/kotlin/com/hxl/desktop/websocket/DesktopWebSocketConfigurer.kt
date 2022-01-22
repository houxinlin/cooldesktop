package com.hxl.desktop.websocket

import com.hxl.desktop.system.property.SystemProperty
import com.hxl.desktop.websocket.ssh.SshManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import org.springframework.http.server.ServerHttpRequest
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.messaging.support.GenericMessage
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration
import org.springframework.web.socket.handler.WebSocketHandlerDecorator
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory
import org.springframework.web.socket.messaging.SessionSubscribeEvent
import org.springframework.web.socket.server.support.DefaultHandshakeHandler
import java.security.Principal
import java.util.*


@Configuration
@EnableWebSocketMessageBroker
class DesktopWebSocketConfigurer : WebSocketMessageBrokerConfigurer {

    @Autowired
    lateinit var systemProperty: SystemProperty

    @Autowired
    lateinit var sshManager: SshManager

    override fun configureMessageBroker(config: MessageBrokerRegistry) {
        config.enableSimpleBroker("/desktop-topic");
        config.setApplicationDestinationPrefixes("/desktop");

    }


    override fun configureWebSocketTransport(registry: WebSocketTransportRegistration) {
        super.configureWebSocketTransport(registry)
        registry.addDecoratorFactory(object : WebSocketHandlerDecoratorFactory {
            override fun decorate(handler: WebSocketHandler): WebSocketHandler {
                return object : WebSocketHandlerDecorator(handler) {
                    override fun afterConnectionEstablished(session: WebSocketSession) {
                        super.afterConnectionEstablished(session)
                        sshManager.registerSession(session)
                    }

                    override fun afterConnectionClosed(session: WebSocketSession, closeStatus: CloseStatus) {
                        super.afterConnectionClosed(session, closeStatus)
                        sshManager.removeBySession(session)
                    }
                }
            }
        })

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
        if (sub.message is GenericMessage) {
            var simpDestination = sub.message.headers["simpDestination"]
            if ("/topic/ssh" == simpDestination) {
                sshManager.startNewSshClient(
                    sub.message.headers["simpSessionId"] as String,
                    systemProperty.getSSHUserInfo()
                )
            }
        }
    }
}