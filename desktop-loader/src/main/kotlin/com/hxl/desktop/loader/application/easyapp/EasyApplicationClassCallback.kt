package com.hxl.desktop.loader.application.easyapp

import com.hxl.desktop.loader.application.ApplicationWrapper
import org.springframework.core.io.UrlResource
import java.util.jar.JarEntry

@FunctionalInterface
interface EasyApplicationClassCallback {
    fun call(urlResource: UrlResource, jarEntry: JarEntry)
}