package com.hxl.desktop.websocket.action

import com.hxl.desktop.common.core.Constant
import com.hxl.desktop.system.core.WebSocketMessageBuilder
import com.hxl.desktop.system.terminal.ServerConnectionInfo
import com.hxl.desktop.system.terminal.ServerConnectionInfoWrap
import com.hxl.desktop.system.sys.SystemProperty
import com.hxl.desktop.system.terminal.Terminal
import com.hxl.desktop.system.terminal.factory.TerminalInstanceFactory
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

    @Autowired
    lateinit var coolDesktopEventAction: CoolDesktopEventAction


    fun createServerConnectionInfoWrap(
        connectionInfo: ServerConnectionInfo,
        session: WebSocketSession
    ): ServerConnectionInfoWrap {
        return ServerConnectionInfoWrap(connectionInfo, SshMessageListener(session))
    }

    override fun action(webSocketSession: WebSocketSession) {
        //获取属性
        var connectionInfo = systemProperty.getServerConnectionInfo()
        //连接信息不完整
        if (!connectionInfo.verification()) {
            coolDesktopEventAction.sendForSubject(
                WebSocketMessageBuilder().builder()
                    .applySubject(Constant.WebSocketSubjectNameConstant.TERMINAL_MESSAGE)
                    .addItem("msg", Constant.StringConstant.TERMINAL_MESSAGE_CONNECT_NOT_FOUND)
            )
            return
        }
        //创建终端实例
        var terminal =
            TerminalInstanceFactory.getTerminal(createServerConnectionInfoWrap(connectionInfo, webSocketSession))
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