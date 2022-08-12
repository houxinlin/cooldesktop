package com.hxl.desktop.system.config

import com.hxl.desktop.common.core.Directory
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.io.File

@Configuration
class SystemResourceMvcConfigurer : WebMvcConfigurer {
    companion object {
        const val WALLPAPER_REQUEST_RESOURCE_PATH = "/system/resource/wallpaper/"
    }

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        super.addResourceHandlers(registry)
        registry.addResourceHandler("$WALLPAPER_REQUEST_RESOURCE_PATH**").addResourceLocations(getWallpaperPath())
    }

    fun getWallpaperPath(): String {
        return "file:" + Directory.getWallpaperWorkDirectory() + File.separator
    }
}