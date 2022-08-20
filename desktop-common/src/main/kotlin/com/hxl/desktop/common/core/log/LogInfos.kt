package com.hxl.desktop.common.core.log

import com.hxl.desktop.common.core.log.enums.CoolDesktopLogInfoType
import com.hxl.desktop.common.core.log.enums.CoolDesktopLogType

class LogInfos {
    var coolDesktopLogType: CoolDesktopLogType = CoolDesktopLogType.LOGIN_LOG
    var coolDesktopLogInfoType: CoolDesktopLogInfoType = CoolDesktopLogInfoType.INFO_LOG
    var logName: String = ""
    var logValue: String = ""
    var userName: String = "admin"
}