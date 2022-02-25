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
import org.springframework.stereotype.Component
import java.lang.Exception


/**
 * 拦截所有NotifyWebSocket注解，把结果通知到客户端
 */
@Aspect
@Component
class DesktopNotifyWebSocketAspect {
    private var log = LoggerFactory.getLogger(DesktopNotifyWebSocketAspect::class.java)

    @Autowired
    lateinit var coolDesktopEventAction: WebSocketSender

    @AfterThrowing(throwing = "exception", pointcut = "@annotation(com.hxl.desktop.common.core.NotifyWebSocket)")
    fun notifyAfter(joinPoint: JoinPoint, exception: Throwable) {
        val signature = joinPoint.signature as MethodSignature
        var notifyWebSocket = signature.method.getDeclaredAnnotation(NotifyWebSocket::class.java)
        var args = joinPoint.args
        coolDesktopEventAction.sender(
            JSON.toJSONString(
                mutableMapOf(
                    "id" to args.last(),
                    "result" to FileHandlerResult.create(-1, exception.message.toString(), "发生异常${exception.message.toString()}"),
                    "subject" to notifyWebSocket.subject,
                    "action" to notifyWebSocket.action
                )
            )
        )
    }

    @AfterReturning(returning = "data", pointcut = "@annotation(com.hxl.desktop.common.core.NotifyWebSocket)")
    fun notifyAfterReturning(joinPoint: JoinPoint, data: Any) {
        val signature = joinPoint.signature as MethodSignature
        var notifyWebSocket = signature.method.getDeclaredAnnotation(NotifyWebSocket::class.java)
        if (data is AsyncResultWithID<*> && data.get() is FileHandlerResult) {
            log.info("处理结果{}", data.get())
            coolDesktopEventAction.sender(
                JSON.toJSONString(
                    mutableMapOf(
                        "id" to data.taskId,
                        "result" to data.get(),
                        "subject" to notifyWebSocket.subject,
                        "action" to notifyWebSocket.action
                    )
                )
            )
        }
    }
}