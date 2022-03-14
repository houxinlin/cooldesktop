package com.hxl.desktop.loader.application

import com.desktop.application.definition.application.Application
import com.desktop.application.definition.application.ApplicationLoader
import com.desktop.application.definition.application.easyapp.EasyApplication
import com.desktop.application.definition.application.webmini.WebMiniApplication
import com.hxl.desktop.common.core.Constant
import com.hxl.desktop.common.core.Directory
import com.hxl.desktop.common.core.NotifyWebSocket
import com.hxl.desktop.loader.application.easyapp.EasyApplicationLoader
import com.hxl.desktop.loader.application.webmini.WebMiniApplicationLoader
import com.hxl.desktop.system.core.CoolDesktopBeanRegister
import common.extent.toPath
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.util.FileSystemUtils
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import java.nio.ByteBuffer
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import java.util.jar.JarFile
import javax.annotation.PostConstruct
import kotlin.io.path.deleteExisting

@Component
class ApplicationInstallDispatcher {
    @Autowired
    private lateinit var applicationRegister: ApplicationRegister
    private lateinit var applicationLoaders: List<ApplicationLoader<*>>

    @Autowired
    fun setApplicationLoader(loader: List<ApplicationLoader<*>>) {
        applicationLoaders = loader
    }

    val log: Logger = LoggerFactory.getLogger(ApplicationInstallDispatcher::class.java)


    //卸载应用分发
    @Synchronized
    fun uninstallApplicationDispatcher(id: String): String {
        log.info("卸载应用,{}", id)
        var applicationWrapper = applicationRegister.getApplicationById(id)
        applicationLoaders.forEach {
            if (it.support(applicationWrapper!!.application)) {
                return it.unregisterApplication(applicationWrapper!!.application).message
            }
        }
        return Constant.StringConstant.NOT_FOUND_LOADERS
    }

    //注册应用分发
    @NotifyWebSocket(subject = Constant.WebSocketSubjectNameConstant.REFRESH_APPLICATION)
    fun installCustomApplicationDispatcher(file: MultipartFile): String {
        return installDispatcher(file.inputStream.readBytes())
    }

    //安装分发
    @Synchronized
    fun installDispatcher(applicationByte: ByteArray): String {
        applicationLoaders.forEach {
            if (it.support(applicationByte)) {
                return it.loadApplicationFromByte(applicationByte).message
            }
        }
        return Constant.StringConstant.NOT_FOUND_LOADERS
    }
}