package com.hxl.desktop.system.core

interface WebSocketSender {
    fun send(msg: String, id: String = "")
    fun sendForDelay(msg: String, id: String = "", second: Long)
}