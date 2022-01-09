package com.hxl.desktop.websocket.ssh

import com.hxl.desktop.system.ssh.SshClient
import com.hxl.desktop.system.ssh.SshClientFactory
import com.hxl.desktop.system.ssh.SshThread
import com.hxl.desktop.system.ssh.TerminalOutput
import org.springframework.messaging.simp.SimpMessageType
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompEncoder
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import java.util.*

class SshBinder(var session: WebSocketSession) {
    fun create(): SshThread {
        return SshClientFactory().createSshSshClient(
            "root",
            "houxinlin.com",
            22,
            "",
            object : TerminalOutput {
                override fun output(data: ByteArray) {
                    if (session.isOpen)
                        session.sendMessage(TextMessage(createMessage(data)))
                }
            })
    }

    fun createStompMessageHeader(): MutableMap<String, Any> {
        var hashMap = mutableMapOf<String, Any>();
        hashMap.put("subscription", createList("sub-0"));
        hashMap.put("content-type", createList("text/plain"));
        var stringObjectHashMap = mutableMapOf<String, Any>();
        stringObjectHashMap.put("simpMessageType", SimpMessageType.MESSAGE);
        stringObjectHashMap.put("stompCommand", StompCommand.MESSAGE);
        stringObjectHashMap.put("subscription", "sub-0");
        stringObjectHashMap.put("nativeHeaders", hashMap);
        return stringObjectHashMap
    }

    private fun createList(value: String): MutableList<String> {
        var list = mutableListOf<String>();
        list.add(value);
        return list;
    }

    fun createMessage(msg: ByteArray): ByteArray {
        var stompEncoder = StompEncoder()
        var encode = stompEncoder.encode(createStompMessageHeader(), msg);
        return encode
    }
}