package com.hxl.desktop.loader.application

import com.desktop.application.definition.application.Application
import com.hxl.desktop.common.core.Constant
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.stream.Collectors

@Component
class ApplicationRegister {
    private val log: Logger = LoggerFactory.getLogger(ApplicationRegister::class.java)
    private val webMiniApplicationMap = mutableMapOf<String, ApplicationWrapper>()
    private val easyApplicationMap = mutableMapOf<String, ApplicationWrapper>()


    /**
     * 注册web应用
     */
    fun registerWebApplication(webMiniApplication: ApplicationWrapper): String {
        log.info("尝试注册Web Application{}", webMiniApplication.application.applicationName)
        webMiniApplication.application.type=Application.WEB_MINI_APP
        return register(webMiniApplication, webMiniApplicationMap)
    }

    /**
     * 注册easy应用
     */
    fun registerEasyApplication(easyApplication: ApplicationWrapper): String {
        easyApplication.application.type=Application.EASY_APP
        return register(easyApplication, easyApplicationMap)
    }

    /**
     * 应用统一初始化
     */
    @Synchronized
    private fun register(application: ApplicationWrapper, map: MutableMap<String, ApplicationWrapper>): String {
        if (isLoaded(application.application.applicationId)) {
            log.info("应用{}已经加载", application.application.applicationName)
            return Constant.StringConstant.LOAD_APPLICATION_DUPLICATE
        }
        log.info("注册Easy Application{}", application.application.applicationName)
        application.init()
        map[application.application.applicationId] = application
        return Constant.StringConstant.LOAD_APPLICATION_SUCCESS
    }

    fun unregister(id: String) {
        webMiniApplicationMap.remove(id)
        easyApplicationMap.remove(id)
    }


    fun isLoaded(id: String): Boolean {
        return getApplicationById(id) != null
    }

    fun listApplication(): List<Application> {
        val easy = easyApplicationMap.values.stream().map { it.application }.collect(Collectors.toList())
        val web = webMiniApplicationMap.values.stream().map { it.application }.collect(Collectors.toList())
        return easy.plus(web)
    }

    fun getApplicationById(id: String): ApplicationWrapper? {
        return webMiniApplicationMap.getOrDefault(id, easyApplicationMap.getOrDefault(id, null))
    }

    fun getWebMinApplication(id: String): ApplicationWrapper? {
        return webMiniApplicationMap[id]
    }

    fun getWebApplicationCount(): Int {
        return webMiniApplicationMap.size
    }

    fun getEasyApplicationCount(): Int {
        return easyApplicationMap.size
    }

    fun getTotalApplication(): Int {
        return easyApplicationMap.size + webMiniApplicationMap.size
    }

}