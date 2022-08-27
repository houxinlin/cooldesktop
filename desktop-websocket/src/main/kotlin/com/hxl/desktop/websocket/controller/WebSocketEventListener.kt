package com.hxl.desktop.websocket.controller

import com.hxl.desktop.websocket.action.IStompSubjectAction
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import org.springframework.messaging.support.GenericMessage
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent
import org.springframework.web.socket.messaging.SessionConnectEvent
import org.springframework.web.socket.messaging.SessionDisconnectEvent
import org.springframework.web.socket.messaging.SessionSubscribeEvent

@Configuration
class WebSocketEventListener {
    lateinit var websocketHanders: List<IStompSubjectAction>

    private val sesstionIdToDestination = mutableMapOf<String, String>()

    @Autowired
    fun setEventAction(websocketHanders: List<IStompSubjectAction>) {
        this.websocketHanders = websocketHanders
    }

    /**
     * 事件订阅监听
     */
    @EventListener
    fun websocketSubscribeEvent(event: SessionSubscribeEvent) {
        submit(event) { it.onSubject(event) }
    }

    /**
     * 断开
     */
    @EventListener
    fun websocketDisconnectEvent(event: SessionDisconnectEvent) {
        submit(event) { it.onClose(event) }
    }

    private fun submit(event: AbstractSubProtocolEvent, function: (IStompSubjectAction) -> Unit) {
        if (event.message is GenericMessage) {
            var simpDestination = ""
            if (event is SessionSubscribeEvent) {
                simpDestination = event.message.headers["simpDestination"] as String
                sesstionIdToDestination[event.message.headers["simpSessionId"] as String] = simpDestination
            }
            if (event is SessionDisconnectEvent) {
                val simSesstionId = event.message.headers["simpSessionId"] as String
                simpDestination = sesstionIdToDestination[simSesstionId]!!
            }
            for (action in websocketHanders) {
                if (action.support(simpDestination)) {
                    function.invoke(action)
                }
            }
        }
    }
}