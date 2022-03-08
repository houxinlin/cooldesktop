package com.hxl.desktop.system.sys

import com.hxl.desktop.database.CoolDesktopDatabase
import com.hxl.desktop.database.CoolDesktopDatabaseConfigKeys
import com.hxl.desktop.system.terminal.ServerConnectionInfo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class SystemProperty {

    @Autowired
    lateinit var coolDesktopDatabase: CoolDesktopDatabase


    fun getServerConnectionInfo(): ServerConnectionInfo {
        return ServerConnectionInfo().apply {
            this.userName = coolDesktopDatabase.getSysValue(CoolDesktopDatabaseConfigKeys.SSH_USER_NAME.keyName)
            this.privateKey = coolDesktopDatabase.getSysValue(CoolDesktopDatabaseConfigKeys.SSH_PRIVATE_VALUE.keyName)
            this.publicKey = coolDesktopDatabase.getSysValue(CoolDesktopDatabaseConfigKeys.SSH_PUBLIC_VALUE.keyName)
            this.host = "localhost"
            this.port = 22
        }
    }

}