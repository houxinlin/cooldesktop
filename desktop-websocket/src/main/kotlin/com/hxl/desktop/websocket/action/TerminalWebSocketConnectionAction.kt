package com.hxl.desktop.websocket.action

import com.hxl.desktop.system.property.SystemProperty
import com.hxl.desktop.system.ssh.Terminal
import com.hxl.desktop.system.ssh.factory.TerminalInstanceFactory
import com.hxl.desktop.websocket.ssh.SshMessageListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.socket.WebSocketSession
import java.util.concurrent.ConcurrentHashMap

@Service
class TerminalWebSocketConnectionAction : WebSocketConnectionAction() {
    var terminalMapping = ConcurrentHashMap<WebSocketSession, Terminal>();

    @Autowired
    lateinit var systemProperty: SystemProperty

    override fun action(webSocketSession: WebSocketSession) {
        var userInfo = systemProperty.getSSHUserInfo().apply { terminalResponse = SshMessageListener(webSocketSession) }
        var terminal = TerminalInstanceFactory.getTerminal(userInfo)
        terminalMapping[webSocketSession] = terminal

    }

    override fun onMessage(message: String, sessionId: String) {
        getWebSocketSession(sessionId)?.run {
            terminalMapping[this]!!.writeCommand(message)
        }
    }

    override fun closeSession(webSocketSession: WebSocketSession) {
        getWebSocketSession(webSocketSession.id)?.run {
            terminalMapping[this]!!.stopTerminal()
        }
    }

    fun getWebSocketSession(id: String): WebSocketSession? {
        for (item in terminalMapping) {
            if (item.key.id == id) {
                return item.key;
            }
        }
        return null
    }

    override fun support(): String {
        return "/topic/ssh"
    }
}