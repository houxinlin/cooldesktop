package com.hxl.desktop.web


import com.desktop.application.definition.application.easyapp.EasyApplication
import com.hxl.desktop.web.Application.Companion.NOT_SUPPORT
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.web.servlet.ServletComponentScan
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.scheduling.annotation.EnableAsync
import java.io.IOException
import java.net.URL
import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.Paths
import java.text.MessageFormat

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
            var application = SpringApplication.run(
                Application::class.java,
                *args
            ) as AnnotationConfigServletWebServerApplicationContext
            //移除默认的requestMappingHandlerMapping
            application.removeBeanDefinition("requestMappingHandlerMapping")
            return
        }
        error((MessageFormat.format(NOT_SUPPORT, this)))

    }

}
