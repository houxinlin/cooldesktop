package com.hxl.desktop.system.core.handler

import com.hxl.desktop.system.ano.NotifyWebSocket
import com.hxl.desktop.system.core.WebSocketMessageBuilder
import org.springframework.scheduling.annotation.AsyncResult

class AsyncStringMessageConvert:WebSocketNotifyMessageConvert {
    override fun support(data: Any): Boolean {
        return (data is AsyncResult<*> && data.get() is String)
    }

    override fun createMessage(data: Any, notifyWebSocket: NotifyWebSocket): String {
        return WebSocketMessageBuilder.Builder()
            .applySubject(notifyWebSocket.subject)
            .applyAction(notifyWebSocket.action)
            .addItem("data", (data as AsyncResult<String>).get())
            .build()
    }
}