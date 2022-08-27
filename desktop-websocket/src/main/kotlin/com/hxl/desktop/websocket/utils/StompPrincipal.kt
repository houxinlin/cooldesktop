package com.hxl.desktop.websocket.utils

import java.security.Principal

class StompPrincipal(private val mUUID: String) : Principal {
    override fun getName(): String {
        return mUUID
    }
}