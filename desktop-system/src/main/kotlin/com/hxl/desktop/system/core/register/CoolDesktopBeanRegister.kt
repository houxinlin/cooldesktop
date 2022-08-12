package com.hxl.desktop.system.core.register

import com.hxl.desktop.common.core.Constant
import com.hxl.desktop.system.core.WebSocketMessageBuilder
import com.hxl.desktop.system.core.WebSocketSender
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.support.RootBeanDefinition
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component

@Component
class CoolDesktopBeanRegister : ApplicationContextAware {
    private  lateinit var applicationContext: ServletWebServerApplicationContext

    @Autowired
    private lateinit var webSocketSender: WebSocketSender

    companion object {
        private val log: Logger = LoggerFactory.getLogger(CoolDesktopBeanRegister::class.java)
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext as ServletWebServerApplicationContext
    }

    fun <T> register(beanClass: Class<T>) {
        if (applicationContext.defaultListableBeanFactory.containsBean(beanClass.name)) {
            log.warn("{}已经在容器中存在，无法注册", beanClass)
            webSocketSender.send(
                WebSocketMessageBuilder.Builder()
                    .applySubject(Constant.WebSocketSubjectNameConstant.NOTIFY_MESSAGE_ERROR)
                    .addItem("data", "无法注册bean${beanClass}")
                    .build()
            )
            return
        }
        applicationContext.defaultListableBeanFactory.isCacheBeanMetadata = false
        applicationContext.defaultListableBeanFactory.registerBeanDefinition(
            beanClass.name, RootBeanDefinition(beanClass)
        )
    }

    fun <T> getBean(beanClass: Class<T>): T? {
        if (!applicationContext.defaultListableBeanFactory.containsBean(beanClass.name)) {
            log.warn("{}不在在容器中，无法获取", beanClass)
            return null
        }
        val bean = applicationContext.defaultListableBeanFactory.getBean(beanClass.name)

        if (beanClass.classLoader == bean.javaClass.classLoader) {
            return bean as T
        }
        log.warn("出现两个应用相同包名情况,包为{}", beanClass)
        return null

    }

    fun <T> destroyBean(beanClass: Class<T>) {
        val beanName = applicationContext.defaultListableBeanFactory.getBeanNamesForType(beanClass)
        beanName.forEach {
            applicationContext.defaultListableBeanFactory.removeBeanDefinition(it)
        }
    }
}