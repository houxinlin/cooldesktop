package com.hxl.desktop.websocket.action

import org.springframework.web.socket.messaging.SessionDisconnectEvent
import org.springframework.web.socket.messaging.SessionSubscribeEvent

interface IStompSubjectAction {

    /**
    * @description: 订阅
    * @date: 2022/8/26 上午9:07
    */

    fun onSubject(event: SessionSubscribeEvent)


    /**
    * @description: 关闭
    * @date: 2022/8/26 上午9:15
    */

    fun onClose(event: SessionDisconnectEvent)

    /**
     * 是否支持次订阅
     */
    fun support(subject:String):Boolean
}