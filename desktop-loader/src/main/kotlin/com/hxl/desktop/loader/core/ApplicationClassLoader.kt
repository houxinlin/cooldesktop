package com.hxl.desktop.loader.core

import org.springframework.boot.loader.LaunchedURLClassLoader
import java.net.URL

class ApplicationClassLoader(exploded: Boolean, urls: Array<out URL>, parent: ClassLoader?) :
    LaunchedURLClassLoader(exploded, urls, parent) {

}