package com.hxl.desktop.loader.core

import com.hxl.desktop.common.core.Constant
import com.hxl.desktop.common.core.log.LogInfosTemplate
import com.hxl.desktop.common.core.log.SystemLogRecord
import com.hxl.desktop.common.utils.ThreadUtils
import com.hxl.desktop.loader.application.ApplicationInstallDispatcher
import com.hxl.desktop.loader.application.ApplicationManager
import com.hxl.desktop.system.core.WebSocketMessageBuilder
import com.hxl.desktop.system.core.WebSocketSender
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.util.concurrent.LinkedBlockingQueue
import javax.annotation.PostConstruct

/**
 * 软件下载管理器
 */
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
    lateinit var webSocketSender: WebSocketSender

    @Autowired
    lateinit var applicationManager: ApplicationManager

    @Autowired
    lateinit var applicationInstallDispatcher: ApplicationInstallDispatcher

    @Autowired
    lateinit var logRecord: SystemLogRecord

    @Autowired
    lateinit var applicationDownloadFactory: ApplicationDownloadFactory

    @Volatile
    var currentApplicationCount = 0

    protected fun sendMessageToWebSocket(msg: String) {
        webSocketSender.send(msg)
    }

    @PostConstruct
    fun init() {
        ThreadUtils.createThread("install-software", this::startConsumer)
    }

    fun startConsumer() {
        //一次只能安装一个软件
        while (true) {
            val applicationInstallId = applicationInstallQueue.take()
            val step = InstallStep.of(applicationDownloadFactory.createDownloadStep(this))
                .addApplicationInstallStep(ApplicationInstallStep(this))
                .addApplicationInstallStep(ClientApplicationRefreshStep(this))
            currentInstallSoftware = applicationInstallId
            currentApplicationCount = applicationManager.getTotalApplication()
            step.execute(applicationInstallId)
        }
    }

    @Async
    fun install(id: String) {
        //如果已经安装
        if (applicationManager.isLoaded(id)) applicationInstallDispatcher.uninstallApplicationDispatcher(id)
        if (!applicationInstallQueue.contains(id)) {
            logRecord.addLog(LogInfosTemplate.SystemInfoLog("安装软件", "软件id${id}"))
            applicationInstallQueue.offer(id)
        }
    }

    fun installDispatcher(byteArray: ByteArray) {
        applicationInstallDispatcher.installDispatcher(byteArray)
    }

    //通知客户端刷新列表
    fun refreshClient() {
        if (applicationManager.getTotalApplication() > currentApplicationCount) {
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