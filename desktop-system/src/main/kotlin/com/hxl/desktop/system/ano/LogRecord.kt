package com.hxl.desktop.system.ano


/**
* @description: 日志记录
* @date: 2022/9/6 下午11:01
*/

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class LogRecord(
    val logName: String = ""
)
