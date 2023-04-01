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

}
