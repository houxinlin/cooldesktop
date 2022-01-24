package com.hxl.desktop.loader.application.webmini

import com.alibaba.fastjson.JSON
import com.hxl.desktop.file.extent.walkFileTree
import com.hxl.desktop.file.utils.Directory
import com.hxl.desktop.loader.application.Application
import com.hxl.desktop.loader.application.ApplicationLoader
import com.hxl.desktop.loader.application.ApplicationRegister
import com.hxl.fm.io.ByteArrayIO
import com.hxl.fm.pk.FilePackage.readByte
import com.hxl.fm.pk.FilePackage.readInt
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.nio.ByteBuffer
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger
import javax.annotation.PostConstruct

@Service
class WebMiniApplicationLoader : ApplicationLoader {
    var log: Logger = LoggerFactory.getLogger(WebMiniApplication::class.java)
    lateinit var executorCountDownLatch: CountDownLatch;

    var loadThreadNameCount = AtomicInteger(0);
    var loadThreadPool =
        ThreadPoolExecutor(
            3, 4, 10, TimeUnit.MINUTES,
            ArrayBlockingQueue<Runnable>(100)
        )

    @Autowired
    lateinit var applicationRegister: ApplicationRegister

    @PostConstruct
    override fun loadApplication() {
        var webapp = Paths.get(Directory.getWebAppDirectory()).walkFileTree()
        executorCountDownLatch = CountDownLatch(webapp.size)
        webapp.forEach { loadWebApp(it) }
        executorCountDownLatch.await()
        log.info("WebApp加载完毕${webapp.size}")
    }

    fun loadWebApp(path: String) {
        loadThreadPool.submit(LoadThread(path))
    }

    inner class LoadThread(var path: String) : Runnable {
        override fun run() {
            try {
                var fileBytes = Files.readAllBytes(Paths.get(path))
                var byteBuffer = ByteBuffer.wrap(fileBytes)
                if (byteBuffer.readInt() != 495934) {
                    return
                }
                var applicationInfoHeaderSize = byteBuffer.readInt()
                var applicationInfoByte = byteBuffer.readByte(applicationInfoHeaderSize)
                println(applicationInfoByte.decodeToString())
                var webMiniApplication =
                    JSON.parseObject(applicationInfoByte.decodeToString(), WebMiniApplication::class.java)
                webMiniApplication.applicationPath = path
                webMiniApplication.staticResOffset = 12L + applicationInfoHeaderSize
                applicationRegister.registerWebApp(webMiniApplication)

            }catch (e:Exception){
                e.printStackTrace()
            } finally {
                executorCountDownLatch.countDown()
            }
        }
    }
}