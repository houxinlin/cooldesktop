package com.hxl.desktop.system.core

import org.springframework.beans.factory.config.BeanDefinitionCustomizer
import org.springframework.beans.factory.support.BeanDefinitionBuilder
import org.springframework.beans.factory.support.RootBeanDefinition
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.support.registerBean
import org.springframework.stereotype.Component
import java.net.URL
import java.net.URLClassLoader
import java.util.*
import javax.annotation.PostConstruct

@Component
class CoolDesktopBeanRegister : ApplicationContextAware {
    lateinit var applicationContext: ServletWebServerApplicationContext
    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext as ServletWebServerApplicationContext
    }

    fun <T> register(beanClass: Class<T>) {
        applicationContext.defaultListableBeanFactory.isCacheBeanMetadata=false
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
            applicationContext.defaultListableBeanFactory.destroySingleton(it)
            applicationContext.defaultListableBeanFactory.removeBeanDefinition(it);
        }
    }
}