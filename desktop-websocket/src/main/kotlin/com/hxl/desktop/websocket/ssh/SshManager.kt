package com.hxl.desktop.websocket.ssh

import com.hxl.desktop.system.terminal.ServerConnectionInfo
import com.hxl.desktop.system.terminal.Terminal
import org.springframework.stereotype.Service
import org.springframework.web.socket.WebSocketSession
import java.util.concurrent.ConcurrentHashMap

@Service
class SshManager {

    val sessionMapping = ConcurrentHashMap<String, WebSocketSession>();

    val terminalMapping = ConcurrentHashMap<String, Terminal>();

    fun registerSession(id: String, session: WebSocketSession) {
        sessionMapping[id] = session
    }
    fun writeCommand(id: String, message: String) {
        terminalMapping[id]?.writeCommand(message)
    }

    fun removeBySession(session: WebSocketSession) {
        sessionMapping.remove(session.id)
        terminalMapping[session.id]?.stopTerminal()
        terminalMapping.remove(session.id)
    }

}