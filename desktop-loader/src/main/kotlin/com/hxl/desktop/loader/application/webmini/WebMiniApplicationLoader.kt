package com.hxl.desktop.loader.application.webmini

import com.alibaba.fastjson.JSON
import com.desktop.application.definition.application.ApplicationLoader
import com.desktop.application.definition.application.webmini.WebMiniApplication
import com.hxl.desktop.common.core.Directory
import com.hxl.desktop.file.extent.walkFileTree
import com.hxl.desktop.loader.application.ApplicationRegister
import com.hxl.desktop.loader.application.ApplicationWrapper
import com.hxl.fm.pk.FilePackage.readByte
import com.hxl.fm.pk.FilePackage.readInt
import common.extent.toFile
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.nio.ByteBuffer
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.Collectors
import javax.annotation.PostConstruct

@Service
class WebMiniApplicationLoader : ApplicationLoader {
    var log: Logger = LoggerFactory.getLogger(WebMiniApplicationLoader::class.java)

    @Autowired
    lateinit var applicationRegister: ApplicationRegister

    lateinit var executorCountDownLatch: CountDownLatch;

    var loadThreadPool = ThreadPoolExecutor(3, 4, 10, TimeUnit.MINUTES, ArrayBlockingQueue(100))

    @PostConstruct
    override fun loadApplication() {
        refresh()
    }

    fun refresh() {
        var webapp = Paths.get(Directory.getWebAppDirectory()).walkFileTree(".webapp", true)
        log.info("web应用列表{}", webapp.stream().map { it.toFile().name }.collect(Collectors.toList()))
        executorCountDownLatch = CountDownLatch(webapp.size)
        webapp.forEach(this::loadWebApp)
        executorCountDownLatch.await()
    }

    fun loadWebApp(path: String) {
        loadThreadPool.submit(ApplicationLoadThread(path))
    }

    inner class ApplicationLoadThread(var path: String) : Runnable {
        override fun run() {
            try {
                var fileBytes = Files.readAllBytes(Paths.get(path))
                var byteBuffer = ByteBuffer.wrap(fileBytes)
                if (byteBuffer.readInt() != 495934) {
                    return
                }
                var applicationInfoHeaderSize = byteBuffer.readInt()
                var applicationInfoByte = byteBuffer.readByte(applicationInfoHeaderSize)
                var webMiniApplication =
                    JSON.parseObject(applicationInfoByte.decodeToString(), WebMiniApplication::class.java)

                webMiniApplication.applicationPath = path
                webMiniApplication.staticResOffset = 12L + applicationInfoHeaderSize
                applicationRegister.registerWebApp(ApplicationWrapper(webMiniApplication))

            } catch (e: Exception) {
                log.info("加载应用失败,{}", e.message)
            } finally {
                executorCountDownLatch.countDown()
            }
        }
    }
}