package com.hxl.desktop.loader.core

import com.hxl.desktop.common.core.Constant
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
        private val log: Logger = LoggerFactory.getLogger(ApplicationDownloadManager::class.java)
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
    lateinit var applicationRegister: ApplicationRegister

    @Autowired
    lateinit var applicationInstallDispatcher: ApplicationInstallDispatcher


    @Autowired
    lateinit var easyApplicationLoader: EasyApplicationLoader

    @Volatile
    var currentApplicationCount = 0;

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
            currentApplicationCount = applicationRegister.getTotalApplication()
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

    fun installDispatcher(byteArray: ByteArray) {
        applicationInstallDispatcher.installDispatcher(byteArray)
    }

    //通知客户端刷新列表
    fun refreshClient() {
        if (applicationRegister.getTotalApplication() > currentApplicationCount) {
            log.info("安装成功，通知客户的刷新安装状态{},{}", currentInstallSoftware, InstallStep.INSTALL_OK_STATE)
            webSocketSender.send(createNotifyMessage(currentInstallSoftware!!, InstallStep.INSTALL_OK_STATE))
            sendMessageToWebSocket(
                WebSocketMessageBuilder.Builder().applySubject(REFRESH_SUBJECT)
                    .build()
            )
            return
        }
        this.refreshProgressState(InstallStep.INSTALL_FAIL_STATE)

    }

    fun refreshProgressState(i: Int) {
        webSocketSender.send(createNotifyMessage(currentInstallSoftware!!, i))
    }

    private fun createNotifyMessage(id: String, progress: Int): String {
        return WebSocketMessageBuilder.Builder()
            .applySubject(Constant.WebSocketSubjectNameConstant.APPLICATION_PROGRESS)
            .addItem("id", id)
            .addItem("progress", progress)
            .build()
    }
}