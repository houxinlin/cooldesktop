package com.hxl.desktop.loader.application

import com.desktop.application.definition.application.ApplicationLoader
import com.hxl.desktop.common.core.Constant
import com.hxl.desktop.common.core.log.SystemLogRecord
import com.hxl.desktop.system.ano.NotifyWebSocket
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile

@Component
class ApplicationInstallDispatcher {
    private val  log: Logger = LoggerFactory.getLogger(ApplicationInstallDispatcher::class.java)


    @Autowired
    private lateinit var applicationManager: ApplicationManager
    private lateinit var applicationLoaders: List<ApplicationLoader<*>>
    @Autowired
    lateinit var logRecored: SystemLogRecord
    @Autowired
    fun setApplicationLoader(loader: MutableList<ApplicationLoader<*>>) {
        applicationLoaders = loader
    }

    /**
     * 卸载应用分发
     */
    @Synchronized
    fun uninstallApplicationDispatcher(id: String): String {
        log.info("卸载应用,{}", id)

        val applicationWrapper = applicationManager.getApplicationById(id)
        applicationWrapper?.run {
            applicationLoaders.forEach {
                if (it.support(applicationWrapper.application)) {
                    return it.unregisterApplication(applicationWrapper.application).message
                }
            }
        }
        return Constant.StringConstant.NOT_FOUND_LOADERS
    }

    /**
     * 注册应用分发,并主动通知客户端进行应用程序刷新
     */
    @NotifyWebSocket(subject = Constant.WebSocketSubjectNameConstant.REFRESH_APPLICATION)
    fun installCustomApplicationDispatcher(file: MultipartFile): String {
        return installDispatcher(file.inputStream.readBytes())
    }

    /**
     * 安装分发
     *
     * 返回安装状态
     */
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