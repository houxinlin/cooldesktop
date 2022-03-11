package com.hxl.desktop.loader.core

import com.hxl.desktop.system.core.WebSocketMessageBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL

class ApplicationDownloadStep(var applicationDownloadManager: ApplicationDownloadManager) :
    InstallStep<String, ByteArray?> {
    companion object {
        val log: Logger = LoggerFactory.getLogger(ApplicationDownloadStep::class.java)
    }

    var id: String = ""
    override fun execute(value: String): ByteArray? {
        this.id = value
        return download(getDownloadUrl(value))
    }

    private fun download(url: String): ByteArray? {
        try {
            var httpURLConnection = URL(url).openConnection() as HttpURLConnection
            var softwareSize: Float = httpURLConnection.contentLengthLong.toFloat()
            var inputStream = httpURLConnection.inputStream
            var byteArray = ByteArray(4096)
            var softwareByteArray = ByteArrayOutputStream()
            var readSize = 0
            while (inputStream.read(byteArray).also { readSize = it } >= 0) {
                softwareByteArray.write(byteArray, 0, readSize)
                var progress = softwareByteArray.size().div(softwareSize) * 100
                applicationDownloadManager.webSocketSender.send(createNotifyMessage(id, progress))
            }
            inputStream.close()
            return softwareByteArray.toByteArray()
        } catch (e: Exception) {
            log.info("下载失败:${e.message}  $url" )
            applicationDownloadManager.webSocketSender.send(createNotifyMessage(id, -1f))
        }
        return null
    }

    private fun createNotifyMessage(id: String, progress: Float): String {
        return WebSocketMessageBuilder().builder()
            .applySubject("/event/install/progress")
            .addItem("id", id)
            .addItem("progress", progress)
            .build()
    }

    private fun getDownloadUrl(id: String): String {
        var url = StringBuffer()
        url.append(applicationDownloadManager.coolProperties.softwareServer!!)
        url.append(applicationDownloadManager.coolProperties.softwareDownloadUrl)
        url.append("?id=$id")
        return url.toString()
    }
}