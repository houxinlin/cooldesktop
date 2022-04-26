package com.hxl.desktop.loader.utils


import com.desktop.application.definition.application.Application
import com.desktop.application.definition.application.easyapp.EasyApplication
import org.springframework.util.StringUtils
import java.util.*
import java.util.function.Function

class ApplicationConvertFunction : Function<Properties, EasyApplication> {
    override fun apply(properties: Properties): EasyApplication {
        return loadApplicationFromProperty(properties)
    }

    private fun getListProperties(data: String, delimiters: String): MutableList<String> {
        if (StringUtils.hasLength(data)) {
            return data.split(delimiters).toMutableList()
        }
        return mutableListOf()
    }

    private fun <T> getPropertiesOrDefault(properties: Properties, key: String, def: T): T {
        return if (properties.containsKey(key)) {
            (properties.getProperty(key)) as T
        } else {
            def
        }
    }

    private fun getBooleanProperties(properties: Properties, key: String): Boolean {
        return properties.getProperty(key).lowercase() == "true"
    }

    private fun loadApplicationFromProperty(properties: Properties): EasyApplication {
        return EasyApplication().apply {
            //程序ID
            this.applicationId = properties.getProperty(Application.APP_ID_PROPERTY_KEY)
            //程序名称
            this.applicationName = properties.getProperty(Application.APP_NAME_PROPERTY_KEY)
            //在启动器中是否可见
            this.visibilityIsDesktop = getBooleanProperties(properties, Application.APP_VISIBILITY_PROPERTY_KEY)
            //app版本
            this.applicationVersion = properties.getProperty(Application.APP_VERSION_PROPERTY_KEY)
            //作者
            this.author = properties.getProperty(Application.APP_AUTHOR_PROPERTY_KEY)
            //是否支持多开
            this.singleInstance = getBooleanProperties(properties, Application.APP_SINGLE_INSTANCE_PROPERTY_KEY)
            //菜单
            this.menus = getListProperties(properties.getProperty(Application.APP_MENU_PROPERTY_KEY), ",")
            //所支持的媒体类型
            this.supportMediaTypes = getListProperties(properties.getProperty(Application.APP_SUPPORT_TYPE_KEY), ",")
            //url排除
            this.urlExclude=getListProperties(getPropertiesOrDefault(properties,Application.APP_URL_EXCLUDE,""),",");
            //背景颜色
            this.windowBackground = getPropertiesOrDefault(properties, Application.APP_WEB_WINDOW_BACKGROUND, this.windowBackground)
            //最低运行版本
            this.environmentVersion = getPropertiesOrDefault(properties, Application.APP_ENVIRONMENT_VERSION, this.environmentVersion)
        }
    }
}