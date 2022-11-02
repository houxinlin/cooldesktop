package com.hxl.desktop.loader

import com.desktop.application.definition.application.ApplicationLoader
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

/**
 * 统一进行本地应用加载
 */
@Component
class ApplicationLoaderRun(private val loader: MutableList<ApplicationLoader<*>>) :CommandLineRunner{
    override fun run(vararg args: String?) {
        loader.forEach { it.loadApplicationFromLocal() }
    }
}