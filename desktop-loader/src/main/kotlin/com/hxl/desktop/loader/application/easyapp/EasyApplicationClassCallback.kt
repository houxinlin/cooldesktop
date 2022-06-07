package com.hxl.desktop.loader.application.easyapp

import org.springframework.core.io.UrlResource
import java.util.jar.JarEntry

@FunctionalInterface
fun interface EasyApplicationClassCallback {
    fun call(urlResource: UrlResource, jarEntry: JarEntry)
}