package com.hxl.desktop.system.core.scheduled

import com.hxl.desktop.common.core.log.LogInfosTemplate
import com.hxl.desktop.common.core.log.SystemLogRecord
import com.hxl.desktop.database.CoolDesktopDatabase
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class DatabaseScheduled {
    @Autowired
    lateinit var coolDesktopDatabase: CoolDesktopDatabase

    @Autowired
    lateinit var logRecord: SystemLogRecord
    @Scheduled(cron = "0 0 12 * * ?")
    fun autoDeleteSysLog(){
        coolDesktopDatabase.deleteSysExpireLog()
        coolDesktopDatabase.deleteSysExpireShareLink()
        logRecord.addLog(LogInfosTemplate.SystemInfoLog("定时任务","在${LocalDateTime.now()}时执行定时任务完成"))
    }
}