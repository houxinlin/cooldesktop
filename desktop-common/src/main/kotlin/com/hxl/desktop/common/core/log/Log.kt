package com.hxl.desktop.common.core.log

import com.hxl.desktop.common.core.log.enums.CoolDesktopLogInfoType
import com.hxl.desktop.common.core.log.enums.CoolDesktopLogType

abstract class Log(
    private val coolDesktopLogType: CoolDesktopLogType,
    private val coolDesktopLogInfoType: CoolDesktopLogInfoType
) {
    abstract fun getLogName(): String
    abstract fun getLogValue(): String

    fun getLogInfos(): LogInfos {
        return LogInfos().apply {
            coolDesktopLogType = this@Log.coolDesktopLogType
            coolDesktopLogInfoType = this@Log.coolDesktopLogInfoType
            logName = getLogName()
            logValue = getLogValue()
        }
    }
}