package com.hxl.desktop.system.core

import com.hxl.desktop.common.core.ano.NotifyWebSocket
import com.hxl.desktop.common.result.FileHandlerResult
import com.hxl.desktop.system.core.handler.*
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.AfterThrowing
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


/**
 * 拦截所有NotifyWebSocket注解，把结果通知到客户端
 */
@Aspect
@Component
class DesktopNotifyWebSocketAspect {
    private val log = LoggerFactory.getLogger(DesktopNotifyWebSocketAspect::class.java)
    private val webSocketNotifyMessageConvert: List<WebSocketNotifyMessageConvert> = arrayListOf(
        AsyncFileHandlerMessageConvert(),
        AsyncStringMessageConvert(),
        AnyMessageConvert()
    )


    @Autowired
    lateinit var coolDesktopEventAction: WebSocketSender

    @AfterThrowing(throwing = "exception", pointcut = "@annotation(com.hxl.desktop.common.core.ano.NotifyWebSocket)")
    fun notifyAfter(joinPoint: JoinPoint, exception: Throwable) {
        val signature = joinPoint.signature as MethodSignature
        var notifyWebSocket = signature.method.getDeclaredAnnotation(NotifyWebSocket::class.java)
        var args = joinPoint.args
        coolDesktopEventAction.send(
            WebSocketMessageBuilder.Builder()
                .applySubject(notifyWebSocket.subject)
                .applyAction(notifyWebSocket.action)
                .addItem("result", FileHandlerResult.create(-1, exception.message.toString(), "发生异常${exception.message}"))
                .addItem("id", args.last())
                .build()
        )
    }


    @AfterReturning(returning = "data", pointcut = "@annotation(com.hxl.desktop.common.core.ano.NotifyWebSocket)")
    fun notifyAfterReturning(joinPoint: JoinPoint, data: Any) {
        val signature = joinPoint.signature as MethodSignature
        var notifyWebSocket = signature.method.getDeclaredAnnotation(NotifyWebSocket::class.java)
        for (messageConvert in webSocketNotifyMessageConvert) {
            if (messageConvert.support(data)) {
                var createMessage = messageConvert.createMessage(data, notifyWebSocket)
                coolDesktopEventAction.send(createMessage)
                return
            }
        }
    }
}