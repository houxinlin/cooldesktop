package com.hxl.desktop.websocket.action

import com.hxl.desktop.system.core.WebSocketMessageBuilder
import com.hxl.desktop.system.core.WebSocketSender
import com.hxl.desktop.websocket.utils.WebSocketUtils
import org.springframework.stereotype.Service
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import java.util.concurrent.DelayQueue
import javax.annotation.PostConstruct

@Service
class CoolDesktopEventAction : WebSocketConnectionAction(), WebSocketSender {
    var coolDesktopEventSocket: WebSocketSession? = null

    val delayQueue = DelayQueue<DelayEvent>()

    @PostConstruct
    fun init() {
        Thread {
            while (true) {
                var delayEvent = delayQueue.take()
                send(delayEvent.msg)
            }
        }.start()
    }

    override fun onMessage(message: String, sessionId: String) {
    }

    override fun sendForDelay(msg: String, id: String, second: Long) {
        delayQueue.put(DelayEvent(msg,second))
    }

    override fun send(msg: String, id: String) {
        if (coolDesktopEventSocket != null && coolDesktopEventSocket!!.isOpen) {
            coolDesktopEventSocket!!.sendMessage(TextMessage(WebSocketUtils.createMessage(msg.toByteArray())))
        }
    }

    fun sendForSubject(messageBuilder: WebSocketMessageBuilder.Builder) {
        send(messageBuilder.build())
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