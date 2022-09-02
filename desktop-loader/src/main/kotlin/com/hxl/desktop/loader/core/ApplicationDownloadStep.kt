package com.hxl.desktop.loader.core

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.text.MessageFormat
import java.util.concurrent.CompletableFuture

class ApplicationDownloadStep(private val serverHost:String,private val applicationDownloadManager: ApplicationDownloadManager) :
    InstallStep<String, List<ByteArray>> {
    companion object {
        private val log: Logger = LoggerFactory.getLogger(ApplicationDownloadStep::class.java)
        private const val DOWNLOAD: String = "/software/api/getSoftware?id={0}"
    }

    private var id: String = ""
    private var totalDownloadSize: Long = 0

    override fun execute(value: String): List<ByteArray> {
        this.id = value
        totalDownloadSize = 0
        try {
            val urls = arrayListOf(getDownloadUrl(value))
            return installApplication(urls)
        } catch (e: Exception) {
            e.printStackTrace()
            //下载任何异常将通知异常
            applicationDownloadManager.refreshProgressState(InstallStep.INSTALL_FAIL_STATE)
        }
        return arrayListOf()
    }

    private fun installApplication(url: List<String>): List<ByteArray> {
        val result = mutableListOf<ByteArray>()
        val totalContentSize = getTotalContentSize(url)
        url.forEach {
            result.add(download(it, totalContentSize) )
        }
        return result
    }

    private fun download(url: String, totalContentSize: Long): ByteArray {
        try {
            log.info("下载应用{},总共大小{}", url, totalContentSize)
            val httpURLConnection = URL(url).openConnection() as HttpURLConnection
            val inputStream = httpURLConnection.inputStream
            val byteArray = ByteArray(4096)
            val softwareByteArray = ByteArrayOutputStream()
            var readSize = 0
            while (inputStream.read(byteArray).also { readSize = it } >= 0) {
                softwareByteArray.write(byteArray, 0, readSize)
                totalDownloadSize += readSize
                val progress = (totalDownloadSize.div(totalContentSize.toFloat()) * 100)
                applicationDownloadManager.refreshProgressState(progress.toInt())
            }
            inputStream.close()
            return softwareByteArray.toByteArray()
        } catch (e: Exception) {
            log.info("下载失败:${e.message}  $url")
            throw  DownLoadException(e.message.toString())
        }
    }

    private fun getTotalContentSize(url: List<String>): Long {
        var total: Long = 0;
        val completableFuture = mutableListOf<CompletableFuture<Long>>()
        url.forEach {
            completableFuture.add(CompletableFuture.supplyAsync {
                val httpURLConnection = URL(it).openConnection() as HttpURLConnection
                val lengthLong = httpURLConnection.contentLengthLong
                if (lengthLong == -1L) { 0 } else lengthLong
            })
        }
        completableFuture.forEach { total += it.get() }
        return total
    }


    private fun format(pattern: String, vararg arguments: Any): String {
        val temp = MessageFormat(pattern)
        return temp.format(arguments)
    }

    private fun getDownloadUrl(id: String): String {
        return serverHost + format(DOWNLOAD, id)
    }

}