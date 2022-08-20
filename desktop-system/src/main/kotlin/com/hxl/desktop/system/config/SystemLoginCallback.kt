package com.hxl.desktop.system.config

import com.hxl.desktop.common.core.log.LogInfosTemplate
import com.hxl.desktop.common.core.log.SystemLogRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Configuration
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.util.function.Function

@Configuration
class SystemLoginCallback : CommandLineRunner {
    @Autowired
    lateinit var systemLogRecord: SystemLogRecord

    override fun run(vararg args: String?) {
        try {
            var authServlet = Class.forName("org.apache.catalina.core.TomcatGlobalAuthenticationHttpServlet")
            val methodType = MethodType.methodType(Void.TYPE, Function::class.java)
            val setCallbackFunction = MethodHandles.lookup()
                .findStatic(authServlet, "setCallbackFunction", methodType)

            setCallbackFunction.invoke(Function<Map<String, Any>?, String?> { arg ->
                systemLogRecord.addLog(LogInfosTemplate.SystemLoginInfoLog(arg["msg"] as String, arg["data"] as String))
                null
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}