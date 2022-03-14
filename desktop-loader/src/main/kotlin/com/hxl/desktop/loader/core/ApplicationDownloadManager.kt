package com.hxl.desktop.loader.core

import com.hxl.desktop.loader.application.ApplicationInstallDispatcher
import com.hxl.desktop.loader.application.ApplicationRegister
import com.hxl.desktop.loader.application.easyapp.EasyApplicationLoader
import com.hxl.desktop.loader.application.webmini.WebMiniApplicationLoader
import com.hxl.desktop.system.config.CoolProperties
import com.hxl.desktop.system.core.WebSocketMessageBuilder
import com.hxl.desktop.system.core.WebSocketSender
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.util.concurrent.LinkedBlockingQueue
import javax.annotation.PostConstruct

//软件下载管理器
@Service
class ApplicationDownloadManager {
    companion object {
        val log: Logger = LoggerFactory.getLogger(ApplicationDownloadManager::class.java)

        const val REFRESH_SUBJECT = "/event/refresh/application"
        const val INSTALL_STATUS_SUBJECT = "/event/software/status"
        const val INSTALL_DONE_SUBJECT = "/event/install/done"
    }

    private val applicationInstallQueue = LinkedBlockingQueue<String>()

    @Volatile
    private var currentInstallSoftware: String? = null

    @Autowired
    lateinit var coolProperties: CoolProperties


    @Autowired
    lateinit var webSocketSender: WebSocketSender

    @Autowired
    lateinit var webMiniApplicationLoader: WebMiniApplicationLoader

    @Autowired
    lateinit var applicationRegister: ApplicationRegister

    @Autowired
    lateinit var applicationInstallDispatcher: ApplicationInstallDispatcher


    @Autowired
    lateinit var easyApplicationLoader: EasyApplicationLoader

    protected fun sendMessageToWebSocket(msg: String) {
        webSocketSender.send(msg)
    }

    @PostConstruct
    fun init() {
        Thread(this::startConsumer).start()
    }

    fun startConsumer() {
        //一次只能安装一个软件
        while (true) {
            var applicationInstallId = applicationInstallQueue.take()
            var step = InstallStep.of(ApplicationDownloadStep(this))
                .addSoftwareInstallStep(ApplicationInstallStep(this))
                .addSoftwareInstallStep(ClientApplicationRefreshStep(this))
            currentInstallSoftware = applicationInstallId
            step.execute(applicationInstallId);
        }
    }

    @Async
    fun download(id: String) {
        //如果已经安装
        if (applicationRegister.isLoaded(id)) {
            sendMessageToWebSocket(
                WebSocketMessageBuilder.Builder()
                    .applySubject(INSTALL_STATUS_SUBJECT)
                    .addItem("data", "已经安装")
                    .build()
            )
            return
        }
        if (!applicationInstallQueue.contains(id)) {
            applicationInstallQueue.offer(id)
        }
    }

    fun registerEasyApplication(byteArray: ByteArray) {
        applicationInstallDispatcher.installDispatcher(byteArray)
    }

    fun registerWebMiniApplication() {
        webMiniApplicationLoader.refresh()

    }

    //通知客户端刷新列表
    fun refreshClient() {
        sendMessageToWebSocket(
            WebSocketMessageBuilder.Builder().applySubject(INSTALL_DONE_SUBJECT)
                .addItem("id", currentInstallSoftware!!)
                .build()
        )
    }
}