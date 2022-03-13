package com.hxl.desktop.loader.application.easyapp

import com.desktop.application.definition.application.easyapp.EasyApplication
import com.hxl.desktop.system.core.CoolDesktopBeanRegister
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cglib.core.ReflectUtils
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.core.Ordered
import org.springframework.core.PriorityOrdered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.util.ReflectionUtils
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.mvc.condition.RequestCondition
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import java.lang.reflect.Method
import java.net.URL
import java.net.URLClassLoader

/**
 * 自定义RequestMappingRegister，用来统一加前缀，并且提供结构动态删除或者注册API
 */
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
class RequestMappingRegister : RequestMappingHandlerMapping(), PriorityOrdered, ApplicationContextAware {
    private val log: Logger = LoggerFactory.getLogger(RequestMappingRegister::class.java)

    private val classMap = mutableMapOf<Class<*>, String>()
    private val requestMappingMap = mutableMapOf<String, MutableList<RequestMappingInfo>>()

    @Autowired
    private lateinit var coolDesktopBeanRegister: CoolDesktopBeanRegister

    @Synchronized
    fun unregisterApplication(application: String) {
        if (!requestMappingMap.containsKey(application)) {
            return
        }
        log.info("反注册Controller" + requestMappingMap[application])
        requestMappingMap[application]!!.forEach(this::unregisterMapping)
        requestMappingMap.remove(application)
    }


    fun registerCustomRequestMapping(easyApplication: EasyApplication) {
        //先向容器注册bean


//        easyApplication.beans.values.forEach { coolDesktopBeanRegister.register(it as Class<*>) }
//
//        easyApplication.beans.values.forEach { coolDesktopBeanRegister.destroyBean(it as Class<*>) }
//
        easyApplication.beans.values.forEach { coolDesktopBeanRegister.register(it as Class<*>) }


        easyApplication.beans.values.forEach { value ->
            if (value is Class<*>) {
                classMap[value] = easyApplication.applicationId
                detectHandlerMethods(coolDesktopBeanRegister.getBean(value))
            }
        }
//
//        classMap.clear()
    }

    //将在请求的地址前面加入应用程序id路径
    override fun getMappingForMethod(method: Method, handlerType: Class<*>): RequestMappingInfo? {
        var requestMappingInfo = super.getMappingForMethod(method, handlerType)
        //如果是自定义的jar应用
        if (requestMappingInfo != null && classMap.containsKey(method.declaringClass)) {
            var applicationId = classMap[method.declaringClass]
            val mappings = requestMappingMap.getOrPut(applicationId!!) { mutableListOf() }
            mappings.add(requestMappingInfo)
            //增加前缀
            var srcPatterns = requestMappingInfo.patternsCondition!!.patterns.first()
            requestMappingInfo.patternsCondition!!.patterns.clear()
            requestMappingInfo.patternsCondition!!.patterns.add("/" + classMap[method.declaringClass] + srcPatterns)
            log.info("注册API，地址为{}", srcPatterns)
        }
        return requestMappingInfo
    }

    fun test() {

    }

}