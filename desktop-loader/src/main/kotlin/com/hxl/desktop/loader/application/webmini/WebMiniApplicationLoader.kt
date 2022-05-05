package com.hxl.desktop.loader.application.webmini

import com.desktop.application.definition.application.Application
import com.desktop.application.definition.application.ApplicationInstallState
import com.desktop.application.definition.application.ApplicationLoader
import com.desktop.application.definition.application.webmini.WebMiniApplication
import com.hxl.desktop.common.core.Constant
import com.hxl.desktop.common.core.Directory
import com.hxl.desktop.file.extent.walkFileTree
import com.hxl.desktop.loader.application.ApplicationRegister
import com.hxl.desktop.loader.application.ApplicationTypeDetection
import com.hxl.desktop.loader.application.ApplicationWrapper
import com.hxl.desktop.loader.core.ApplicationEvent
import com.hxl.fm.pk.FilePackage.readByte
import com.hxl.fm.pk.FilePackage.readInt
import com.hxl.desktop.common.extent.toPath
import com.hxl.desktop.common.utils.JSON
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.util.FileSystemUtils
import java.nio.ByteBuffer
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.stream.Collectors

@Component
class WebMiniApplicationLoader : ApplicationLoader<WebMiniApplication> {
    private val log: Logger = LoggerFactory.getLogger(WebMiniApplicationLoader::class.java)
    @Autowired
    lateinit var applicationRegister: ApplicationRegister

    lateinit var executorCountDownLatch: CountDownLatch;
    override fun loadApplicationFromByte(byteArray: ByteArray): ApplicationInstallState {
        val webMiniApplication = getApplicationFromByte(ByteBuffer.wrap(byteArray))
        //用于保证不会写入两个相同的应用到本地
        if (applicationRegister.isLoaded(webMiniApplication.applicationId)) {
            log.info("应用无法重复加载{}", webMiniApplication.applicationName)
            return ApplicationInstallState.DUPLICATE
        }
        //注册并且保存到本地
        var appStoragePath = Paths.get(Directory.getWebAppDirectory(), "${UUID.randomUUID()}.webapp")
        Files.write(appStoragePath, byteArray)
        webMiniApplication.applicationPath = appStoragePath.toString()
        registerWebApplication(webMiniApplication)
        return ApplicationInstallState.INSTALL_OK
    }

    override fun loadApplicationFromLocal() {
        refresh()
    }

    override fun support(application: Application): Boolean {
        return application is WebMiniApplication
    }

    override fun support(byteArray: ByteArray): Boolean {
        return ApplicationTypeDetection.detection(byteArray) == Application.WEB_MINI_APP
    }

    override fun unregisterApplication(application: Application): ApplicationInstallState {
        FileSystemUtils.deleteRecursively(application.applicationPath.toPath())
        applicationRegister.unregister(application.applicationId)
        return ApplicationInstallState.UNINSTALL_OK
    }



    var loadThreadPool = ThreadPoolExecutor(3, 4, 10, TimeUnit.MINUTES, ArrayBlockingQueue(100))

    @EventListener
    fun applicationEvent(applicationEvent: ApplicationEvent) {
        if (applicationEvent.action == Constant.ApplicationEvent.ACTION_REFRESH_WEB_MINI_APPLICATION) {
            refresh()
        }
    }

    companion object {
        fun getApplicationFromByte(byteBuffer: ByteBuffer): WebMiniApplication {
            if (byteBuffer.position() == 0) {
                //消费4个字节的魔术
                byteBuffer.readInt()
            }
            val applicationInfoHeaderSize = byteBuffer.readInt()
            val applicationInfoByte = byteBuffer.readByte(applicationInfoHeaderSize)
            val webMiniApplication =
                JSON.parseObject(applicationInfoByte.decodeToString(), WebMiniApplication::class.java)
            webMiniApplication.staticResOffset = 12L + applicationInfoHeaderSize
            return webMiniApplication
        }
    }


    fun refresh() {
        val webapp = Paths.get(Directory.getWebAppDirectory()).walkFileTree(".webapp", true)
        log.info("web应用列表{}", webapp.stream().map { it.toFile().name }.collect(Collectors.toList()))
        executorCountDownLatch = CountDownLatch(webapp.size)
        webapp.forEach(this::loadWebApp)
        executorCountDownLatch.await()
        log.info("web应用加载完毕")
    }

    fun loadWebApp(path: Path) {
        loadThreadPool.submit(ApplicationLoadThread(path))
    }

    fun registerWebApplication(webMiniApplication: WebMiniApplication): String {
        return applicationRegister.registerWebApplication(ApplicationWrapper(webMiniApplication))
    }

    inner class ApplicationLoadThread(var path: Path) : Runnable {
        override fun run() {
            try {
                var applicationByteBuffer = ByteBuffer.wrap(Files.readAllBytes(path))
                //检测类型
                var applicationType = ApplicationTypeDetection.detection(applicationByteBuffer)

                if (applicationType != Application.WEB_MINI_APP) {
                    log.info("{}不是Web应用，无法加载", path)
                    return
                }
                var webMiniApplication = getApplicationFromByte(applicationByteBuffer)
                webMiniApplication.applicationPath = path.toString()
                registerWebApplication(webMiniApplication)
            } catch (e: Exception) {
                log.info("加载应用失败,{}", e.message)
            } finally {
                executorCountDownLatch.countDown()
            }
        }
    }
}