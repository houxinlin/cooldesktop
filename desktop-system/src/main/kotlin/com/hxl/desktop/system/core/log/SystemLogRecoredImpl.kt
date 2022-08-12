package com.hxl.desktop.system.core.log

import com.hxl.desktop.common.core.log.LogInfos
import com.hxl.desktop.common.core.log.SystemLogRecored
import com.hxl.desktop.database.CoolDesktopDatabase
import org.springframework.stereotype.Service
import javax.annotation.Resource

@Service
class SystemLogRecoredImpl : SystemLogRecored {

    @Resource
    lateinit var coolDesktopDatabase: CoolDesktopDatabase
    override fun addlog(logInfos: LogInfos) {
        coolDesktopDatabase.addSysLog(logInfos.coolDesktopLogType.logTypeName,
                logInfos.coolDesktopLogInfoType.logTypeName,
                logInfos.logName, logInfos.logValue, logInfos.userName)
    }
}