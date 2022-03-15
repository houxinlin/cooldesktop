package com.hxl.desktop.system.core

import org.springframework.beans.factory.BeanFactoryUtils
import org.springframework.beans.factory.support.RootBeanDefinition
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerMapping
import java.util.*

@Component
class CoolDesktopBeanRegister : ApplicationContextAware {
    lateinit var applicationContext: ServletWebServerApplicationContext
    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext as ServletWebServerApplicationContext

        applicationContext.removeBeanDefinition("requestMappingHandlerMapping")

    }

    fun <T> register(beanClass: Class<T>) {
        applicationContext.defaultListableBeanFactory.isCacheBeanMetadata = false
        applicationContext.defaultListableBeanFactory.registerBeanDefinition(
            beanClass.name,
            RootBeanDefinition(beanClass)
        )
    }

    fun <T> getBean(beanClass: Class<T>): T {
        return applicationContext.defaultListableBeanFactory.getBean(beanClass)
    }

    fun <T> destroyBean(beanClass: Class<T>) {
        var beanName = applicationContext.defaultListableBeanFactory.getBeanNamesForType(beanClass)
        beanName.forEach {
            applicationContext.defaultListableBeanFactory.removeBeanDefinition(it);
        }
    }
}