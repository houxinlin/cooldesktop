package com.hxl.desktop.websocket.utils

import org.springframework.messaging.simp.SimpMessageType
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompEncoder

object WebSocketUtils {
     fun createMessage(msg: ByteArray): ByteArray {
        var stompEncoder = StompEncoder()
        var encode = stompEncoder.encode(createStompMessageHeader(), msg);
        return encode
    }

    private fun createStompMessageHeader(): MutableMap<String, Any> {
        var hashMap = mutableMapOf<String, Any>();
        hashMap["subscription"] = createList("sub-0");
        hashMap["content-type"] = createList("text/plain");
        var stringObjectHashMap = mutableMapOf<String, Any>();
        stringObjectHashMap["simpMessageType"] = SimpMessageType.MESSAGE;
        stringObjectHashMap["stompCommand"] = StompCommand.MESSAGE;
        stringObjectHashMap["subscription"] = "sub-0";
        stringObjectHashMap["nativeHeaders"] = hashMap;
        return stringObjectHashMap
    }

    private fun createList(value: String): MutableList<String> {
        var list = mutableListOf<String>();
        list.add(value);
        return list;
    }

}