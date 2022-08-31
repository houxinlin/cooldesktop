package com.hxl.desktop.system.config

import com.hxl.desktop.common.core.log.LogInfosTemplate
import com.hxl.desktop.common.core.log.SystemLogRecord
import com.hxl.desktop.system.core.share.CoolDesktopShareHttpServlet
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Configuration
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.util.function.Function
import javax.annotation.Resource
import javax.servlet.http.HttpServlet

/**
 * 设置tomcat的一些回调
 */
@Configuration
class TomcatCallback : CommandLineRunner {
    @Autowired
    lateinit var systemLogRecord: SystemLogRecord

    @Autowired
    lateinit var cooldesktopShareHttpServlet: CoolDesktopShareHttpServlet

    override fun run(vararg args: String?) {
        setLoginCallback()
        setShareLinkCallback()
    }

  private  fun setShareLinkCallback(){
        val sharelinkClass :Class<*>? = loadClass("org.apache.catalina.core.TomcatShareFileHttpServlet")
        sharelinkClass?.apply {
            val methodType = MethodType.methodType(Void.TYPE, HttpServlet::class.java)
            val setCallbackFunction = MethodHandles.lookup()
                .findStatic(this, "setCooldesktop", methodType)
            setCallbackFunction.invoke(cooldesktopShareHttpServlet)
        }

    }
    private fun loadClass(name:String):Class<*>?{
       try {
           return Class.forName(name)
       }catch (e: ClassNotFoundException){
           e.printStackTrace()
           //no-op
       }
        return null
    }
    private   fun setLoginCallback(){
        try {
            //设置tomcat登录接口回调
            val authServlet :Class<*>? = loadClass("org.apache.catalina.core.TomcatGlobalAuthenticationHttpServlet")
           authServlet?.apply {
               val methodType = MethodType.methodType(Void.TYPE, Function::class.java)
               val setCallbackFunction = MethodHandles.lookup()
                   .findStatic(this, "setCallbackFunction", methodType)
               setCallbackFunction.invoke(Function<Map<String, Any>?, String?> { arg ->
                   systemLogRecord.addLog(LogInfosTemplate.SystemLoginInfoLog(arg["msg"] as String, arg["data"] as String))
                   null
               })
           }
        } catch (e: Exception) {
            //no-op
        }
    }

}