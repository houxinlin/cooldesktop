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
    private val connectionActions = mutableListOf<WebSocketConnectionAction>()

    private val webSocketSessionMap = ConcurrentHashMap<String, WebSocketSession>()


    /**
     * 记录所有可处理WebSocket订阅的事件支持者
     */
    @Autowired
    fun setWebSocketConnectionAction(action: List<WebSocketConnectionAction>) {
        connectionActions.addAll(action)
    }

    override fun configureMessageBroker(config: MessageBrokerRegistry) {
        config.enableSimpleBroker("/desktop-topic")
        config.setApplicationDestinationPrefixes("/desktop")
    }

    override fun configureWebSocketTransport(registry: WebSocketTransportRegistration) {
        super.configureWebSocketTransport(registry)
        registry.addDecoratorFactory { handler ->
            object : WebSocketHandlerDecorator(handler) {
                /**
                 * 记录连接
                 */
                override fun afterConnectionEstablished(session: WebSocketSession) {
                    super.afterConnectionEstablished(session)
                    webSocketSessionMap[session.id] = session

                }

                /**
                 * 关闭链接
                 */
                override fun afterConnectionClosed(session: WebSocketSession, closeStatus: CloseStatus) {
                    super.afterConnectionClosed(session, closeStatus)
                    webSocketSessionMap.remove(session.id)
                    connectionActions.forEach { it.closeSession(session) }
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
                ): Principal {
                    return StompPrincipal(UUID.randomUUID().toString())
                }
            })
            .setAllowedOrigins("http://192.168.0.110:3000", "http://localhost:3000")//开发的时候
            .withSockJS()
    }

    /**
     * 事件订阅
     */
    @EventListener
    fun websocketSubscribeEvent(sub: SessionSubscribeEvent) {
        if (sub.message is GenericMessage) {
            val simpDestination = sub.message.headers["simpDestination"] as String
            for (connectionAction in connectionActions) {
                if (connectionAction.support(simpDestination)) connectionAction.action(
                    simpDestination,
                    webSocketSessionMap[sub.message.headers["simpSessionId"]]!!
                )
            }
        }
    }
}