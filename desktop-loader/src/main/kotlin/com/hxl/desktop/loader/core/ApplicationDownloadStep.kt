package com.hxl.desktop.loader.core

import com.fasterxml.jackson.databind.ObjectMapper
import com.hxl.desktop.common.core.Constant
import com.hxl.desktop.system.core.WebSocketMessageBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.text.MessageFormat
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

class ApplicationDownloadStep(var applicationDownloadManager: ApplicationDownloadManager) :
    InstallStep<String, List<ByteArray>> {
    companion object {
        val log: Logger = LoggerFactory.getLogger(ApplicationDownloadStep::class.java)
        const val GET_DEPENDENT_ID: String = "/software/api/dependent/get?id={0}"
        const val DOWNLOAD: String = "/software/api/getSoftware?id={0}"
    }

    var id: String = ""
    var totalDownloadSize: Long = 0

    override fun execute(value: String): List<ByteArray> {
        this.id = value
        totalDownloadSize = 0
        val dependentIds = getDependentIds()
        //主应用
        val urls = arrayListOf(getDownloadUrl(value))
        if (dependentIds.isNotEmpty()) {
            //依赖应用
            dependentIds.forEach { urls.add(getDownloadUrl(it.toString())) }
        }
        return installs(urls)
    }

    private fun installs(url: List<String>): List<ByteArray> {
        try {
            val result = mutableListOf<ByteArray>()
            val totalContentSize = getTotalContentSize(url)
            url.forEach {
                val softwareByteArray: ByteArray? = download(it, totalContentSize) ?: return arrayListOf()
                result.add(softwareByteArray!!)
            }
            return result
        } catch (e: Exception) {
            applicationDownloadManager.refreshProgressState(InstallStep.INSTALL_FAIL_STATE)
        }
        return arrayListOf()
    }

    private fun download(url: String, totalContentSize: Long): ByteArray? {
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
                var progress = (totalDownloadSize.div(totalContentSize.toFloat()) * 100)
                applicationDownloadManager.refreshProgressState(progress.toInt())
            }
            inputStream.close()
            return softwareByteArray.toByteArray()
        } catch (e: Exception) {
            log.info("下载失败:${e.message}  $url")
            throw  DownLoadException(e.message.toString())
        }
        return null
    }

    private fun getTotalContentSize(url: List<String>): Long {
        var total: Long = 0;
        val completableFuture = mutableListOf<CompletableFuture<Long>>()
        url.forEach {
            completableFuture.add(CompletableFuture.supplyAsync {
                val httpURLConnection = URL(it).openConnection() as HttpURLConnection
                val lengthLong = httpURLConnection.contentLengthLong
                if (lengthLong == -1L) {
                    0
                } else lengthLong
            })
        }
        completableFuture.forEach {
            total += it.get()
        }
        return total
    }


    private fun format(pattern: String, vararg arguments: Any): String {
        val temp = MessageFormat(pattern)
        return temp.format(arguments)
    }

    private fun getDownloadUrl(id: String): String {
        return applicationDownloadManager.coolProperties.softwareServer + format(DOWNLOAD, id)
    }

    private fun getDependentIds(): List<*> {
        val objectMapper = ObjectMapper()
        val url = applicationDownloadManager.coolProperties.softwareServer!! + format(GET_DEPENDENT_ID, this.id)
        val map = objectMapper.readValue(URL(url).readText(), Map::class.java)
        return objectMapper.readValue(map["ids"] as String, List::class.java)
    }
}