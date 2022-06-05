package com.hxl.desktop.system.core

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Component
import org.springframework.web.HttpRequestHandler
import org.springframework.web.servlet.DispatcherServlet
import org.springframework.web.servlet.HandlerMapping
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler
import java.nio.file.Path

@Component
class CoolDesktopApplicationStaticResourceRegister : ApplicationContextAware {
    companion object {
        const val JAR_FILE = "jar:file:"
        const val APPLICATION_STATIC_RESOURCE = "/asr/"
    }

    private lateinit var applicationContext: ApplicationContext;

    @javax.annotation.Resource(name = "dispatcherServlet")
    lateinit var dispatcherServlet: DispatcherServlet

    private val resourceMapping = mutableMapOf<String, SimpleUrlHandlerMapping>()

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }

    fun register(urlPath: String, localtion: Path) {
        val newUrlPath = getUrlPath(urlPath)

        val urlMap: MutableMap<String, HttpRequestHandler> = LinkedHashMap()
        val handler: ResourceHttpRequestHandler = getRequestHandler(localtion)
        urlMap[newUrlPath] = handler
        val simpleUrlHandlerMapping = SimpleUrlHandlerMapping(urlMap, 0)
        simpleUrlHandlerMapping.applicationContext = this.applicationContext
        simpleUrlHandlerMapping.initApplicationContext()

        resourceMapping[newUrlPath] = simpleUrlHandlerMapping
        getHandlerMappings()?.add(0, simpleUrlHandlerMapping)
    }

    private fun getUrlPath(urlPath: String): String {
        var newUrlPath = urlPath
        if (!newUrlPath.endsWith("/**")) {
            newUrlPath = "$newUrlPath/**"
        }
        return "$APPLICATION_STATIC_RESOURCE$newUrlPath"
    }

    fun unregister(urlPath: String) {
        val newUrlPath = getUrlPath(urlPath)
        if (resourceMapping.containsKey(newUrlPath)) {
            getHandlerMappings()?.remove(resourceMapping[newUrlPath] as SimpleUrlHandlerMapping)
        }
    }

    private fun getHandlerMappings(): MutableList<HandlerMapping>? {
        val handlerMappingsField = dispatcherServlet::class.java.getDeclaredField("handlerMappings")
        handlerMappingsField.isAccessible = true
        val list = handlerMappingsField.get(dispatcherServlet)
        if (list != null) {
            return list as MutableList<HandlerMapping>
        }
        return null
    }

    private fun getRequestHandler(jarPath: Path): ResourceHttpRequestHandler {
        val handler = ResourceHttpRequestHandler()
        val resources: MutableList<Resource> = arrayListOf()
        resources.add(UrlResource("${JAR_FILE}${jarPath}!/"))
        handler.setLocationValues(arrayListOf())
        handler.locations = resources
        handler.afterPropertiesSet()
        return handler
    }
}