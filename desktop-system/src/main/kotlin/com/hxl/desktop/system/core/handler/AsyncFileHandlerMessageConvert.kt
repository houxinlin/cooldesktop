package com.hxl.desktop.system.core.handler

import com.hxl.desktop.system.ano.NotifyWebSocket
import com.hxl.desktop.common.model.FileHandlerResult
import com.hxl.desktop.system.core.AsyncResultWithID
import com.hxl.desktop.system.core.WebSocketMessageBuilder
import org.springframework.scheduling.annotation.AsyncResult

class AsyncFileHandlerMessageConvert : WebSocketNotifyMessageConvert {
    override fun support(data: Any): Boolean {
        return (data is AsyncResult<*> && data.get() is FileHandlerResult)
    }

    override fun createMessage(data: Any, notifyWebSocket: NotifyWebSocket): String {
        val messageBuilder = WebSocketMessageBuilder.Builder()
            .applyAction(notifyWebSocket.action)
            .applySubject(notifyWebSocket.subject)
            .addItem("result", (data as AsyncResult<*>).get())
        if (data is AsyncResultWithID<*>) messageBuilder.addItem("id", data.taskId)
        return messageBuilder.build()
    }
}