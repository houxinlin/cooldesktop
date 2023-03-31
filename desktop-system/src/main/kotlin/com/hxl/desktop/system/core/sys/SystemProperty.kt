package com.hxl.desktop.system.core.sys

import com.hxl.desktop.database.CoolDesktopDatabase
import com.hxl.desktop.database.CoolDesktopDatabaseConfigKeys
import com.hxl.desktop.system.core.command.CommandConstant
import com.hxl.desktop.system.core.command.TerminalCommand
import com.hxl.desktop.system.core.terminal.ServerConnectionInfo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.stream.Collectors

@Component
class SystemProperty {

    @Autowired
    lateinit var coolDesktopDatabase: CoolDesktopDatabase


    fun getServerConnectionInfo(): ServerConnectionInfo {
        return ServerConnectionInfo().apply {
            // lsof -i -P -n  | grep WeChatApp | grep LISTEN | awk '{print $9}' | cut -d ":" -f 2 | sort -u
            // netstat -tnlp | grep sshd | awk '{print $4}' | awk -F: '{print $NF}' | sort -u
            this.userName = coolDesktopDatabase.getSysConfig(CoolDesktopDatabaseConfigKeys.SSH_USER_NAME.keyName)
            this.privateKey = coolDesktopDatabase.getSysConfig(CoolDesktopDatabaseConfigKeys.SSH_PRIVATE_VALUE.keyName)
            this.publicKey = coolDesktopDatabase.getSysConfig(CoolDesktopDatabaseConfigKeys.SSH_PUBLIC_VALUE.keyName)
            this.host = "localhost"
        }
    }

}
