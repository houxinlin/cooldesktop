package com.hxl.desktop.system.core.handler

import com.hxl.desktop.common.core.ano.NotifyWebSocket

interface WebSocketNotifyMessageConvert {
    fun support(data: Any): Boolean

    fun createMessage(data: Any,notifyWebSocket: NotifyWebSocket): String
}