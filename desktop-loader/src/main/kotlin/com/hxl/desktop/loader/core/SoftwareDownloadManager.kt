package com.hxl.desktop.loader.core

import com.hxl.desktop.loader.application.ApplicationRegister
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
class SoftwareDownloadManager {
    companion object {
        val log: Logger = LoggerFactory.getLogger(SoftwareDownloadManager::class.java)

        const val REFRESH_SUBJECT = "/event/refresh/application"
        const val INSTALL_STATUS_SUBJECT = "/event/software/status"
        const val INSTALL_DONE_SUBJECT="/event/install/done"
    }
    private val softwareInstallQueue = LinkedBlockingQueue<String>()

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
    protected fun sendMessageToWebSocket(msg: String) {
        webSocketSender.sender(msg)
    }

    @PostConstruct
    fun init() {
        Thread(this::startConsumer).start()
    }

    fun startConsumer() {
        //一次只能安装一个软件
       while (true){
           var softwareInstallId = softwareInstallQueue.take()
           var step = InstallStep.of(SoftwareDownloadStep(this))
               .addSoftwareInstallStep(SoftwareInstallStep(this))
           currentInstallSoftware = softwareInstallId
           step.execute(softwareInstallId);
       }
    }

    @Async
    fun download(id: String) {
        //如果已经安装
        if (applicationRegister.isLoaded(id)) {
            sendMessageToWebSocket(WebSocketMessageBuilder().builder().applySubject(INSTALL_STATUS_SUBJECT)
                .addItem("data","已经安装")
                .build())
            return
        }
        if (!softwareInstallQueue.contains(id)){
            softwareInstallQueue.offer(id)
        }
    }

    fun refreshWebMiniApplication() {
        webMiniApplicationLoader.refresh()
        //通知客户端刷新列表
        sendMessageToWebSocket(WebSocketMessageBuilder().builder().applySubject(INSTALL_DONE_SUBJECT)
            .addItem("id",currentInstallSoftware!!)
            .build())
    }
}