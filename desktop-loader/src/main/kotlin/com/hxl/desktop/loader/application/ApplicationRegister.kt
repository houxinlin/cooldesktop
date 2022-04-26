package com.hxl.desktop.loader.application

import com.desktop.application.definition.application.Application
import com.desktop.application.definition.application.easyapp.EasyApplication
import com.hxl.desktop.common.core.Constant
import com.hxl.desktop.common.extent.toPath
import com.hxl.desktop.system.core.CoolDesktopApplicationStaticResourceRegister
import com.hxl.desktop.system.manager.OpenUrlManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component
import org.springframework.web.servlet.DispatcherServlet
import org.springframework.web.servlet.HandlerMapping
import java.util.stream.Collectors

@Component
class ApplicationRegister : CommandLineRunner {
    private val log: Logger = LoggerFactory.getLogger(ApplicationRegister::class.java)
    private val webMiniApplicationMap = mutableMapOf<String, ApplicationWrapper>()
    private val easyApplicationMap = mutableMapOf<String, ApplicationWrapper>()

    @javax.annotation.Resource(name = "dispatcherServlet")
    lateinit var dispatcherServlet: DispatcherServlet

    @Autowired
    private lateinit var coolDesktopApplicationStaticResourceRegister: CoolDesktopApplicationStaticResourceRegister

    /**
     * 注册web应用
     */
    fun registerWebApplication(webMiniApplication: ApplicationWrapper): String {
        log.info("尝试注册Web Application{}", webMiniApplication.application.applicationName)
        webMiniApplication.application.type = Application.WEB_MINI_APP
        return register(webMiniApplication, webMiniApplicationMap)
    }

    /**
     * 注册easy应用
     */
    fun registerEasyApplication(easyApplication: ApplicationWrapper): String {
        easyApplication.application.type = Application.EASY_APP

        //注册静态资源
        coolDesktopApplicationStaticResourceRegister.register(
            easyApplication.application.applicationId,
            easyApplication.application.applicationPath.toPath()
        )

        easyApplication.application.urlExclude.forEach(OpenUrlManager::register)
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

    override fun run(vararg args: String?) {
        registerStaticResource()
    }

    fun registerStaticResource() {
        easyApplicationMap.forEach { (key, value) ->
            coolDesktopApplicationStaticResourceRegister.register(key, value.application.applicationPath.toPath())
        }
    }

    fun unregister(id: String) {
        getApplicationById(id)?.run { if (this.application is EasyApplication) this.application.urlExclude.forEach(OpenUrlManager::unregister) }
        webMiniApplicationMap.remove(id)
        easyApplicationMap.remove(id)
        coolDesktopApplicationStaticResourceRegister.unregister(id)
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