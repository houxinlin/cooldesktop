package com.hxl.desktop.system.core.log

import com.hxl.desktop.common.core.log.Log
import com.hxl.desktop.common.core.log.LogInfos
import com.hxl.desktop.common.core.log.SystemLogRecord
import com.hxl.desktop.database.CoolDesktopDatabase
import org.springframework.stereotype.Service
import javax.annotation.Resource

@Service
class SystemLogRecordImpl : SystemLogRecord {

    @Resource
    lateinit var coolDesktopDatabase: CoolDesktopDatabase

    override fun addLog(log: Log) {
        val logInfos = log.getLogInfos()
        coolDesktopDatabase.addSysLog(
            logInfos.coolDesktopLogType.logTypeName,
            logInfos.coolDesktopLogInfoType.logTypeName,
            logInfos.logName, logInfos.logValue, logInfos.userName
        )
    }
}