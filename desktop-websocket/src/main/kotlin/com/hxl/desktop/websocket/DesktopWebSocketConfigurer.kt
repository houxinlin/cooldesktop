package com.hxl.desktop.websocket

import com.hxl.desktop.system.sys.SystemProperty
import com.hxl.desktop.websocket.action.WebSocketConnectionAction
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
import org.springframework.web.socket.messaging.SessionSubscribeEvent
import org.springframework.web.socket.server.support.DefaultHandshakeHandler
import java.security.Principal
import java.util.*
import java.util.concurrent.ConcurrentHashMap


@Configuration
@EnableWebSocketMessageBroker
class DesktopWebSocketConfigurer : WebSocketMessageBrokerConfigurer {

    @Autowired
    lateinit var systemProperty: SystemProperty
    var connectionAction = mutableMapOf<String, WebSocketConnectionAction>()

    var webSocketSessionMap = ConcurrentHashMap<String, WebSocketSession>()


    @Autowired
    fun setWebSocketConnectionAction(action: List<WebSocketConnectionAction>) {
        action.forEach { connectionAction[it.support()] = it }
    }

    override fun configureMessageBroker(config: MessageBrokerRegistry) {
        config.enableSimpleBroker("/desktop-topic");
        config.setApplicationDestinationPrefixes("/desktop");

    }

    override fun configureWebSocketTransport(registry: WebSocketTransportRegistration) {
        super.configureWebSocketTransport(registry)
        registry.addDecoratorFactory { handler ->
            object : WebSocketHandlerDecorator(handler) {
                override fun afterConnectionEstablished(session: WebSocketSession) {
                    super.afterConnectionEstablished(session)
                    webSocketSessionMap[session.id] = session

                }

                override fun afterConnectionClosed(session: WebSocketSession, closeStatus: CloseStatus) {
                    super.afterConnectionClosed(session, closeStatus)
                    webSocketSessionMap.remove(session.id)
                    connectionAction.values.forEach { it.closeSession(session) }
                }
            }
        }

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
            .setAllowedOrigins("http://localhost:3000")//开发的时候
            .withSockJS()
    }

    @EventListener
    fun websocketSubscribeEvent(sub: SessionSubscribeEvent) {
        if (sub.message is GenericMessage) {
            var simpDestination = sub.message.headers["simpDestination"]
            if (connectionAction.containsKey(simpDestination)) {
                connectionAction[simpDestination]!!.action(webSocketSessionMap[sub.message.headers["simpSessionId"]]!!)
            }
        }
    }
}