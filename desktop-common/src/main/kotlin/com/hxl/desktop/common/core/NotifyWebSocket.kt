package com.hxl.desktop.common.core

/**
 * 异步处理时候将返回值通过WebSocket发送给客户端
 */
@Target(AnnotationTarget.FUNCTION)
annotation class NotifyWebSocket(
    val subject: String = "",
    val action: String = ""
)


