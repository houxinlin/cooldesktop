package com.hxl.desktop.system.core.handler

import com.hxl.desktop.common.core.ano.NotifyWebSocket
import com.hxl.desktop.system.core.WebSocketMessageBuilder

class AnyMessageConvert : WebSocketNotifyMessageConvert {
    override fun support(data: Any): Boolean {
        return true
    }

    override fun createMessage(data: Any, notifyWebSocket: NotifyWebSocket): String {
        return WebSocketMessageBuilder.Builder()
            .applySubject(notifyWebSocket.subject)
            .applyAction(notifyWebSocket.action)
            .addItem("data", data)
            .build()
    }
}