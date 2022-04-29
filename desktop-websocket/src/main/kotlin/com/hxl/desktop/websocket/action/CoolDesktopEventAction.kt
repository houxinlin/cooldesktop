package com.hxl.desktop.websocket.action

import com.hxl.desktop.system.core.WebSocketMessageBuilder
import com.hxl.desktop.system.core.WebSocketSender
import com.hxl.desktop.websocket.utils.WebSocketUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import java.util.concurrent.DelayQueue
import java.util.concurrent.LinkedBlockingQueue
import javax.annotation.PostConstruct

@Service
class CoolDesktopEventAction : WebSocketConnectionAction(), WebSocketSender {
    private var coolDesktopEventSocket: WebSocketSession? = null

    private val delayQueue = DelayQueue<DelayEvent>()

    private val offlineMessageQueue = LinkedBlockingQueue<String>()

    private val log = LoggerFactory.getLogger(CoolDesktopEventAction::class.java)


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
        delayQueue.put(DelayEvent(msg, second))
    }

    /**
     * 所有消息统一走这里
     */
    override fun send(msg: String, id: String) {
        if (coolDesktopEventSocket != null && coolDesktopEventSocket!!.isOpen) {
            log.info("WebSocket发送数据{}",msg)
            coolDesktopEventSocket!!.sendMessage(TextMessage(WebSocketUtils.createMessage(msg.toByteArray())))
            return
        }
        offlineMessageQueue.offer(msg)
    }

    fun sendForSubject(messageBuilder: WebSocketMessageBuilder.Builder) {
        send(messageBuilder.build())
    }


    override fun action(webSocketSession: WebSocketSession) {
        coolDesktopEventSocket = webSocketSession
        while (offlineMessageQueue.poll()?.also { send(it) } != null) { }
    }

    override fun closeSession(webSocketSession: WebSocketSession) {
    }

    override fun support(): String {
        return "/topic/events"
    }
}