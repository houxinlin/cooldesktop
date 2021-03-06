package com.hxl.desktop.websocket.action

import com.hxl.desktop.system.core.WebSocketMessageBuilder
import com.hxl.desktop.system.core.WebSocketSender
import com.hxl.desktop.system.tail.TailManager
import com.hxl.desktop.websocket.utils.DelayEvent
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

    private val coolDesktopEventSocket: MutableList<WebSocketSession> = mutableListOf()
    private val delayQueue = DelayQueue<DelayEvent>()
    private val offlineMessageQueue = LinkedBlockingQueue<String>()

    companion object {
        private val log = LoggerFactory.getLogger(CoolDesktopEventAction::class.java)
        private const val MAX_OFFINE_MESSAGE_QUEUE: Int = 5;

    }

    @PostConstruct
    fun init() {
        Thread {
            while (true) {
                val delayEvent = delayQueue.take()
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
        var flag = false
        coolDesktopEventSocket.forEach {
            if (it.isOpen) {
                it.sendMessage(TextMessage(WebSocketUtils.createMessage(msg.toByteArray())))
                flag = true
            }
        }
        //如果一个连接也没有，则加入离线队列
        if (offlineMessageQueue.size >= MAX_OFFINE_MESSAGE_QUEUE) return
        if (!flag) offlineMessageQueue.offer(msg)
    }

    fun sendForSubject(messageBuilder: WebSocketMessageBuilder.Builder) {
        send(messageBuilder.build())
    }

    /**
     * 当新的socket连接后走这里
     */

    override fun action(subject: String, webSocketSession: WebSocketSession) {
        coolDesktopEventSocket.add(webSocketSession)
        //推送离线消息
        while (offlineMessageQueue.poll()?.also { send(it) } != null) {
        }
    }

    override fun closeSession(webSocketSession: WebSocketSession) {
        //最后一个主通信Socket关闭后，同时关闭所有tail追踪
        if (coolDesktopEventSocket.size == 1) TailManager.stopAll()
        coolDesktopEventSocket.remove(webSocketSession)
    }


    override fun support(subject: String): Boolean {
        return subject == "/topic/events"
    }
}