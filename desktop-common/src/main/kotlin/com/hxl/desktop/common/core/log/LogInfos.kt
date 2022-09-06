package com.hxl.desktop.common.core.log

import com.hxl.desktop.common.core.log.enums.CoolDesktopLogInfoType
import com.hxl.desktop.common.core.log.enums.CoolDesktopLogType

/**
* @description: 日志具体信息
* @date: 2022/9/6 下午10:46
*/

class LogInfos {
    var coolDesktopLogType: CoolDesktopLogType = CoolDesktopLogType.LOGIN_LOG
    var coolDesktopLogInfoType: CoolDesktopLogInfoType = CoolDesktopLogInfoType.INFO_LOG
    var logName: String = ""
    var logValue: String = ""
    var userName: String = "admin"
}