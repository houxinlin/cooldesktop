package com.hxl.desktop.websocket.ssh

import com.hxl.desktop.system.ssh.SshClientFactory
import com.hxl.desktop.system.ssh.SshThread
import com.hxl.desktop.system.ssh.TerminalOutput
import org.springframework.stereotype.Service
import org.springframework.web.socket.WebSocketSession
import java.util.concurrent.ConcurrentHashMap

@Service
class SshManager {
    var userId: String = ""

    var sessionMapping = ConcurrentHashMap<String, WebSocketSession>();

    var sshThreadMapping = ConcurrentHashMap<String, SshThread>();

    fun registerSession(id: String, session: WebSocketSession) {
        sessionMapping[id] = session
    }

    fun registerSession(session: WebSocketSession) {
        registerSession(session.id, session)
    }

    fun startNewSshClient(id: String) {
        var sshThread = SshBinder(sessionMapping.get(id)!!).create()
        sshThreadMapping[id] = sshThread

    }

    fun writeCommand(id: String, message: String) {
        sshThreadMapping[id]?.writeCommand(message)
    }

}