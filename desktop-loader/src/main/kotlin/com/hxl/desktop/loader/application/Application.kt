package com.hxl.desktop.loader.application


open class Application {
    /**
     * 应用程序名
     */
    var applicationName: String = ""

    /**
     * 图标
     */
    var icon: String = ""

    /**
     * 版本
     */
    var applicationVersion: String = "1.0"

    /**
     * 是否可以最大化
     */
    var canMax: Boolean = true

    /**
     *可以处理的文件类型
     */
    var handlerMediaTypes: List<String> = mutableListOf()

    /**
     * 作者
     */
    var author: String = ""

    /**
     * 是否可见
     */
    var visibilityIsDesktop: Boolean = true

    /**
     * 是否可以多开
     */
    var singleInstance: Boolean = true

}