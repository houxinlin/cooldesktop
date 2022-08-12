package com.hxl.desktop.system.ano

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class LogRecord(
    val logName: String = ""
)
