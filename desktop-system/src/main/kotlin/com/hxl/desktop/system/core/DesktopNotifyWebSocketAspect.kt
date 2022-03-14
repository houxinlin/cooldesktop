package com.hxl.desktop.system.core

import com.alibaba.fastjson.JSON
import com.hxl.desktop.common.core.NotifyWebSocket
import common.result.FileHandlerResult
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.AfterThrowing
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.AsyncResult
import org.springframework.stereotype.Component


/**
 * 拦截所有NotifyWebSocket注解，把结果通知到客户端
 */
@Aspect
@Component
class DesktopNotifyWebSocketAspect {
    private val log = LoggerFactory.getLogger(DesktopNotifyWebSocketAspect::class.java)

    @Autowired
    lateinit var coolDesktopEventAction: WebSocketSender

    @AfterThrowing(throwing = "exception", pointcut = "@annotation(com.hxl.desktop.common.core.NotifyWebSocket)")
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

    private fun createBaseTypeMessage(notifyWebSocket: NotifyWebSocket, data: Any): String {
        return WebSocketMessageBuilder.Builder()
            .applyAction(notifyWebSocket.action)
            .applySubject(notifyWebSocket.subject)
            .addItem("data", data)
            .build()
    }


    @AfterReturning(returning = "data", pointcut = "@annotation(com.hxl.desktop.common.core.NotifyWebSocket)")
    fun notifyAfterReturning(joinPoint: JoinPoint, data: Any) {
        log.info("通知客户端$data")
        val signature = joinPoint.signature as MethodSignature
        var notifyWebSocket = signature.method.getDeclaredAnnotation(NotifyWebSocket::class.java)
        if (data is String) {
            coolDesktopEventAction.send(createBaseTypeMessage(notifyWebSocket, data))
            return
        }

        if (data is AsyncResult<*> && data.get() is FileHandlerResult) {
            log.info("处理结果{}", data.get())
            var messageBuilder = WebSocketMessageBuilder.Builder()
                .applyAction(notifyWebSocket.action)
                .applySubject(notifyWebSocket.subject)
                .addItem("result", data.get())
            if (data is AsyncResultWithID<*>) {
                messageBuilder.addItem("id", data.taskId)
            }
            coolDesktopEventAction.send(messageBuilder.build())
        }
        if (data is FileHandlerResult) {
            coolDesktopEventAction.send(
                WebSocketMessageBuilder.Builder()
                    .applySubject(notifyWebSocket.subject)
                    .applyAction(notifyWebSocket.action)
                    .addItem("data", data)
                    .build()
            )
        }
    }
}