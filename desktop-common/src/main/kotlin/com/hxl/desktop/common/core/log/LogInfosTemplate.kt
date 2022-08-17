package com.hxl.desktop.common.core.log

import com.hxl.desktop.common.core.log.enums.CoolDesktopLogInfoType
import com.hxl.desktop.common.core.log.enums.CoolDesktopLogType

class LogInfosTemplate {
    open class BaseInfoLog(
        private val coolDesktopLogType: CoolDesktopLogType,
        private val logName: String,
        private val value: String
    ) :
        Log(coolDesktopLogType, CoolDesktopLogInfoType.INFO_LOG) {
        override fun getLogName(): String {
            return logName
        }

        override fun getLogValue(): String {
            return value
        }

    }
    open class BaseErrorLog(
        private val coolDesktopLogType: CoolDesktopLogType,
        private val logName: String,
        private val value: String
    ) :
        Log(coolDesktopLogType, CoolDesktopLogInfoType.ERROR_LOG) {
        override fun getLogName(): String {
            return logName
        }

        override fun getLogValue(): String {
            return value
        }

    }
    class SystemInfoLog(private val name: String, private val value: String) :
        BaseInfoLog(CoolDesktopLogType.SYSTEM_LOG, name, value)

    class ApiInfoLog(private val name: String, private val value: String) :
        BaseInfoLog(CoolDesktopLogType.API_LOG, name, value)

    class SystemErrorLog(private val name: String, private val value: String) :
        BaseErrorLog(CoolDesktopLogType.SYSTEM_LOG, name, value)

    class ApiErrorLog(private val name: String, private val value: String) :
        BaseErrorLog(CoolDesktopLogType.API_LOG, name, value)

    class ApplicationErrorLog(private val name: String, private val value: String) :
        BaseErrorLog(CoolDesktopLogType.APPLICATION_LOG, name, value)
}