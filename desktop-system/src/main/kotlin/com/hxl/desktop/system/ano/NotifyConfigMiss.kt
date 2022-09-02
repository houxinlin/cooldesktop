package com.hxl.desktop.system.ano
@Target(AnnotationTarget.FUNCTION)
annotation class NotifyConfigMiss(val sysConfigKey: String = "")
