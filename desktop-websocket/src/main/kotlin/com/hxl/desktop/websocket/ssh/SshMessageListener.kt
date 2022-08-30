package com.hxl.desktop.websocket.ssh

import com.hxl.desktop.system.core.terminal.TerminalResponse
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession

class SshMessageListener(var session: WebSocketSession) : TerminalResponse {
    override fun output(response: ByteArray) {
        if (session.isOpen)
            session.sendMessage(TextMessage(response))
    }
}