package com.hxl.desktop.system.core

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.AsyncListenableTaskExecutor
import org.springframework.core.task.SimpleAsyncTaskExecutor
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.stereotype.Service
import java.util.concurrent.Callable
import java.util.concurrent.Future

@Configuration
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
}