package com.hxl.desktop.loader.application

import com.desktop.application.definition.application.Application
import com.desktop.application.definition.application.easyapp.EasyApplication
import com.desktop.application.definition.application.webmini.WebMiniApplication
import com.hxl.desktop.common.core.Constant
import common.extent.toPath
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.util.FileSystemUtils
import java.util.*
import java.util.stream.Collectors

@Service
class ApplicationRegister {
    private val log: Logger = LoggerFactory.getLogger(ApplicationRegister::class.java)
    private val webMiniApplicationMap = mutableMapOf<String, ApplicationWrapper>()

    private val easyApplicationMap = mutableMapOf<String, ApplicationWrapper>()

    /**
     * 注册web应用
     */
    @Synchronized
    fun registerWebApp(webMiniApplication: ApplicationWrapper) {
        log.info("注册Web Application{}", webMiniApplication)
        register(webMiniApplication, webMiniApplicationMap)
    }

    /**
     * 注册easy应用
     */
    @Synchronized
    fun registerEasyApplication(easyApplication: ApplicationWrapper) {
        log.info("注册Easy Application{}", easyApplication)
        register(easyApplication, easyApplicationMap)
    }

    private fun register(application: ApplicationWrapper, map: MutableMap<String, ApplicationWrapper>) {
        if (isLoaded(application.application.applicationId)) {
            return
        }
        application.init()
        map[application.application.applicationId] = application
    }

    fun isLoaded(id: String): Boolean {
        return webMiniApplicationMap.containsKey(id)
    }

    fun listApplication(): List<Application> {
        var easy = easyApplicationMap.values.stream().map { it.application }.collect(Collectors.toList())
        var web = webMiniApplicationMap.values.stream().map { it.application }.collect(Collectors.toList())
        return easy.plus(web)
    }

    fun getApplicationById(id: String): ApplicationWrapper? {
        return webMiniApplicationMap.getOrDefault(id, easyApplicationMap.getOrDefault(id, null))
    }

    fun getWebMinApplication(id: String): ApplicationWrapper? {
        return webMiniApplicationMap[id]
    }

    fun unRegisterWebMiniApplication(application: WebMiniApplication): String {
        FileSystemUtils.deleteRecursively(application.applicationPath.toPath())
        webMiniApplicationMap.remove(application.applicationId)
        return Constant.StringConstant.UNINSTALL_SUCCESS
    }

    fun unRegisterEasyApplication(application: EasyApplication): String {
        return Constant.StringConstant.UNINSTALL_FAIL
    }

    fun unregister(id: String): String {
        log.info("卸载应用,{}", id)
        var applicationWrapper = getApplicationById(id)
        if (applicationWrapper != null) {
            if (applicationWrapper.application is WebMiniApplication) {
                return unRegisterWebMiniApplication(applicationWrapper.application as WebMiniApplication)
            }
            if (applicationWrapper.application is EasyApplication) {
                return unRegisterEasyApplication(applicationWrapper.application as EasyApplication)
            }
        }
        return Constant.StringConstant.UNINSTALL_FAIL
    }
}