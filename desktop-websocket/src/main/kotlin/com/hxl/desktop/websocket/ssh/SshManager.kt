package com.hxl.desktop.websocket.ssh

import com.hxl.desktop.system.ssh.SshClientFactory
import com.hxl.desktop.system.ssh.SshServerInfo
import com.hxl.desktop.system.ssh.SshThread
import org.springframework.stereotype.Service
import org.springframework.web.socket.WebSocketSession
import java.util.concurrent.ConcurrentHashMap

@Service
class SshManager {

    var sessionMapping = ConcurrentHashMap<String, WebSocketSession>();

    var sshThreadMapping = ConcurrentHashMap<String, SshThread>();

    fun registerSession(id: String, session: WebSocketSession) {
        sessionMapping[id] = session
    }

    fun registerSession(session: WebSocketSession) {
        registerSession(session.id, session)
    }

    fun startNewSshClient(id: String, serverInfo: SshServerInfo) {
        var sshMessageListener = SshMessageListener(sessionMapping[id]!!)
        serverInfo.apply { terminalOutput = sshMessageListener }
        var sshClient = SshClientFactory().createSshSshClient(serverInfo)
        sshThreadMapping[id] = sshClient

    }

    fun writeCommand(id: String, message: String) {
        sshThreadMapping[id]?.writeCommand(message)
    }

    fun removeBySession(session: WebSocketSession) {
        sessionMapping.remove(session.id)
        sshThreadMapping[session.id]?.stopTerminal()
        sshThreadMapping.remove(session.id)
    }

}