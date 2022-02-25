package com.hxl.desktop.common.core

@Target(AnnotationTarget.FUNCTION)
annotation class NotifyWebSocket(
    val subject: String = "",
    val action: String = ""
)


