package com.hxl.desktop.websocket.action

import com.hxl.desktop.common.utils.LanguageUtils
import com.hxl.desktop.common.utils.ThreadUtils
import com.hxl.desktop.system.core.WebSocketMessageBuilder
import com.hxl.desktop.system.core.WebSocketSender
import com.hxl.desktop.system.tail.TailManager
import com.hxl.desktop.websocket.utils.DelayEvent
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import org.springframework.web.socket.messaging.SessionDisconnectEvent
import org.springframework.web.socket.messaging.SessionSubscribeEvent
import java.util.concurrent.DelayQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicInteger
import javax.annotation.PostConstruct

@Service
class CoolDesktopEventAction : IStompSubjectAction, WebSocketSender {

    private val socketConnectCount = AtomicInteger(0)
    private val delayQueue = DelayQueue<DelayEvent>()
    private val offlineMessageQueue = LinkedBlockingQueue<String>()

    @Autowired
    lateinit var simpMessagingTemplate: SimpMessagingTemplate

    companion object {
        private val log = LoggerFactory.getLogger(CoolDesktopEventAction::class.java)
        private const val MAX_OFFINE_MESSAGE_QUEUE: Int = 5;
        private const val SUBSCRIB_NAME = "/desktop-topic/event"

    }

    @PostConstruct
    fun init() {
        ThreadUtils.createThread("message-delay-queue") {
            LanguageUtils.doWhile({
                send(delayQueue.take().msg)
            })
        }
    }

    override fun sendForDelay(msg: String, id: String, second: Long) {
        delayQueue.put(DelayEvent(msg, second))
    }

    /**
     * 所有消息统一走这里
     */
    override fun send(msg: String, id: String) {
        if (socketConnectCount.get() <= 0) {
            if (offlineMessageQueue.size >= MAX_OFFINE_MESSAGE_QUEUE) return
        }
        simpMessagingTemplate.convertAndSend(SUBSCRIB_NAME, msg)
    }

    fun sendForSubject(messageBuilder: WebSocketMessageBuilder.Builder) {
        send(messageBuilder.build())
    }
    override fun onSubject(event: SessionSubscribeEvent) {
        socketConnectCount.incrementAndGet()
        while (offlineMessageQueue.poll()?.also { send(it) } != null) {
        }
    }

    override fun onClose(event: SessionDisconnectEvent) {
        socketConnectCount.decrementAndGet()
        if (socketConnectCount.get() == 0) TailManager.stopAll()
    }

    override fun support(subject: String): Boolean {
        return subject == SUBSCRIB_NAME
    }
}