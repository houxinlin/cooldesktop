package com.hxl.desktop.system.core.scheduled

import com.hxl.desktop.database.CoolDesktopDatabase
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class DatabaseScheduled {
    @Autowired
    lateinit var coolDesktopDatabase: CoolDesktopDatabase
    @Scheduled(cron = "0 0 12 * * ?")
    fun autoDeleteSysLog(){
        coolDesktopDatabase.deleteSysExpireLog()
    }
}