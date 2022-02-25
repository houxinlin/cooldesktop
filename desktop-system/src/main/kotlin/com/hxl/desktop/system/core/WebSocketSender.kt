package com.hxl.desktop.system.core

interface WebSocketSender {
    fun sender( msg: String,id: String = "",);
}