package com.hxl.desktop.loader.application

import com.desktop.application.definition.application.Application
import com.desktop.application.definition.application.easyapp.EasyApplication
import com.hxl.desktop.common.core.Constant
import com.hxl.desktop.common.kotlin.extent.toPath
import com.hxl.desktop.loader.application.easyapp.EaseApplicationWrapper
import com.hxl.desktop.system.core.register.CoolDesktopApplicationStaticResourceRegister
import com.hxl.desktop.system.core.manager.OpenUrlManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.util.stream.Collectors

@Component
class ApplicationManager : CommandLineRunner {
    companion object {
        private val log: Logger = LoggerFactory.getLogger(ApplicationManager::class.java)
    }


    //web应用集合
    private val webMiniApplicationMap = mutableMapOf<String, ApplicationWrapper>()

    //jar应用集合
    private val easyApplicationMap = mutableMapOf<String, ApplicationWrapper>()

    @Autowired
    private lateinit var coolDesktopApplicationStaticResourceRegister: CoolDesktopApplicationStaticResourceRegister

    /**
     * 注册web应用
     */
    fun registerWebApplication(webMiniApplication: ApplicationWrapper): String {
        webMiniApplication.application.type = Application.WEB_MINI_APP
        return register(webMiniApplication, webMiniApplicationMap)
    }

    /**
    * @description: 客户端开打应用
    * @date: 2022/8/26 上午10:02
    */

    fun pushOpenApplicationEvent(applicationId:String){
        this.easyApplicationMap.getOrDefault(applicationId,null)?.run {
            (this as EaseApplicationWrapper).applicationEventListener?.onOpen(applicationId)
        }
    }

    /**
    * @description: 客户端关闭应用
    * @date: 2022/8/26 上午10:02
    */

    fun pushCloseApplicationEvent(applicationId: String) {
        this.easyApplicationMap.getOrDefault(applicationId,null)?.run {
            (this as EaseApplicationWrapper).applicationEventListener?.onClose(applicationId)
        }
    }
    /**
     * 注册easy应用
     */
    fun registerEasyApplication(easyApplication: ApplicationWrapper): String {
        easyApplication.application.type = Application.EASY_APP

        //注册静态资源
        coolDesktopApplicationStaticResourceRegister.register(
            easyApplication.application.applicationId,
            easyApplication.application.applicationPath.toPath())
        //注册开放路径
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
        log.info("注册应用程序{}", application.application.applicationName)
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

        getApplicationById(id)?.run {
            //开放路径卸载
            if (this.application is EasyApplication) this.application.urlExclude.forEach(OpenUrlManager::unregister)
            this.destory()
        }
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