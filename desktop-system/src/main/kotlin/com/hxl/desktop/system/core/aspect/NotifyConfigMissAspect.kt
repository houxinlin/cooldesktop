package com.hxl.desktop.system.core.aspect

import com.hxl.desktop.common.core.Constant
import com.hxl.desktop.database.CoolDesktopDatabase
import com.hxl.desktop.system.ano.NotifyConfigMiss
import com.hxl.desktop.system.core.WebSocketMessageBuilder
import com.hxl.desktop.system.core.WebSocketSender
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Aspect
@Component
class NotifyConfigMissAspect {
    @Autowired
    lateinit var coolDesktopDatabase: CoolDesktopDatabase
    @Autowired
    lateinit var socketSender: WebSocketSender
    @Pointcut(value = "@annotation(com.hxl.desktop.system.ano.NotifyConfigMiss)")
    private fun pointcut() {
    }

    @Before("pointcut()")
    fun notifyAfterReturning(joinPoint: JoinPoint) {
        val signature = joinPoint.signature as MethodSignature
        val notifyConfigMiss = signature.method.getDeclaredAnnotation(NotifyConfigMiss::class.java)
        if (coolDesktopDatabase.getSysConfig(  notifyConfigMiss.sysConfigKey)==""){
            socketSender.send(WebSocketMessageBuilder.Builder()
                .applySubject(Constant.WebSocketSubjectNameConstant.NOTIFY_MESSAGE_ERROR)
                .addItem("data",Constant.StringConstant.NO_CONFIG_APPLIACTION_SERVER)
                .build())
        }
    }
}