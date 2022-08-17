package com.hxl.desktop.web


import com.hxl.desktop.common.core.log.LogInfosTemplate
import com.hxl.desktop.common.core.log.SystemLogRecord
import com.hxl.desktop.web.Application.Companion.NOT_SUPPORT
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.web.servlet.ServletComponentScan
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.scheduling.annotation.EnableAsync
import java.text.MessageFormat
import java.time.LocalDateTime

@SpringBootApplication(scanBasePackages = ["com.hxl.desktop"], exclude = [DataSourceAutoConfiguration::class])
@ServletComponentScan
@EnableAsync
@EnableAspectJAutoProxy
class Application {

    companion object {
        const val NOT_SUPPORT = "预期为Linux系统，当前为{0}，与期望值不符，将退出"
    }
}

fun main(args: Array<String>) {

    System.getProperty("os.name").run {
        if (this.lowercase().startsWith("linux")) {
            val applicationContext = SpringApplication.run(Application::class.java, *args)
            applicationContext.getBean(SystemLogRecord::class.java)
                .addLog(LogInfosTemplate.SystemInfoLog("系统启动", "系统将在${LocalDateTime.now()}时启动"))
            return
        }
        error((MessageFormat.format(NOT_SUPPORT, this)))

    }

}
