package com.hxl.desktop.websocket.ssh

import com.hxl.desktop.system.ssh.ServerConnectionInfo
import com.hxl.desktop.system.ssh.Terminal
import com.hxl.desktop.system.ssh.factory.TerminalInstanceFactory
import org.springframework.stereotype.Service
import org.springframework.web.socket.WebSocketSession
import java.util.concurrent.ConcurrentHashMap

@Service
class SshManager {

    var sessionMapping = ConcurrentHashMap<String, WebSocketSession>();

    var terminalMapping = ConcurrentHashMap<String, Terminal>();

    fun registerSession(id: String, session: WebSocketSession) {
        sessionMapping[id] = session
    }

    fun registerSession(session: WebSocketSession) {
        registerSession(session.id, session)
    }

    fun startNewSshClient(id: String, serverInfo: ServerConnectionInfo) {
        var sshMessageListener = SshMessageListener(sessionMapping[id]!!)
        serverInfo.apply { terminalResponse = sshMessageListener }
        var sshClient = TerminalInstanceFactory.getTerminal(serverInfo)
//        var sshClient = TerminalInstanceFactory().createSshSshClient(serverInfo)
//        terminalMapping[id] = sshClient

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