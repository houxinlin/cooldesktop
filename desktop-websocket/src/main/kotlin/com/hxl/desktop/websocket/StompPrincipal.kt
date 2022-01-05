package com.hxl.desktop.websocket

import java.security.Principal

class StompPrincipal(private val mUUID: String) : Principal {
    override fun getName(): String {
        return mUUID
    }
}