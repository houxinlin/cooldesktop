package com.hxl.desktop.loader.core

import com.hxl.desktop.database.CoolDesktopDatabase
import com.hxl.desktop.database.CoolDesktopDatabaseConfigKeys
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ApplicationDownloadFactory {
    @Autowired
    lateinit var coolDesktopDatabase: CoolDesktopDatabase
    fun createDownloadStep(applicationDownloadManager: ApplicationDownloadManager): ApplicationDownloadStep {
        val url = coolDesktopDatabase.getSysConfig(CoolDesktopDatabaseConfigKeys.APPLICATION_SERVER_HOST.keyName)
        return ApplicationDownloadStep(url,applicationDownloadManager)
    }
}