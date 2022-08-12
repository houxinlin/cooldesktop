package com.hxl.desktop.common.core.log

import com.hxl.desktop.common.core.log.enums.CoolDesktopLogInfoType
import com.hxl.desktop.common.core.log.enums.CoolDesktopLogType

interface SystemLogRecored {
    fun addlog(logInfos: LogInfos)
}