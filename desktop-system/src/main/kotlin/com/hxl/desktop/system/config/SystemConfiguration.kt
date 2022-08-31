package com.hxl.desktop.system.config

import com.hxl.desktop.system.core.register.RequestMappingRegister
import com.hxl.desktop.system.core.share.CoolDesktopShareHttpServlet
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass
import org.springframework.boot.web.servlet.ServletRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Conditional
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.AsyncListenableTaskExecutor
import org.springframework.core.task.SimpleAsyncTaskExecutor
import org.springframework.scheduling.annotation.EnableScheduling
import java.util.concurrent.Callable
import java.util.concurrent.Future
import javax.servlet.http.HttpServlet

@Configuration
@EnableScheduling
class SystemConfiguration {

    @Bean("syncThreadThreadPoolTaskExecutor")
    fun syncThreadThreadPoolTaskExecutor(): AsyncListenableTaskExecutor {
        class Executor : SimpleAsyncTaskExecutor() {
            override fun <T : Any?> submit(task: Callable<T>): Future<T> {
                return super.submit(task)
            }
        }
        return Executor()
    }

    @Bean(name = ["requestMappingRegister", "requestMappingHandlerMapping"])
    fun requestMappingHandlerMapping(): RequestMappingRegister {
        return RequestMappingRegister()
    }

    /**
    * @description: 文件分享
    * @date: 2022/8/30 上午1:14
    */

    @Bean
    fun cooldesktopShareHttpServlet():HttpServlet{
        return  CoolDesktopShareHttpServlet()
    }

    /**
    * @description: 开发的时候需要注册一个/s/的共享文件下载路径， 生产环境中由tomcat负责转发到cooldesktopShareHttpServlet，免除认证
    * @date: 2022/9/1 上午3:14
    */
    @Bean
    @Conditional(ConditionalOnDeveloper::class)
    fun countryServlet(): ServletRegistrationBean<HttpServlet>? {
        val servRegBean: ServletRegistrationBean<HttpServlet> = ServletRegistrationBean<HttpServlet>()
        servRegBean.servlet = cooldesktopShareHttpServlet()
        servRegBean.addUrlMappings("/s/*")
        servRegBean.setLoadOnStartup(1)
        return servRegBean
    }

}