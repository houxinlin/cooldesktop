package com.hxl.desktop.system.utils

import com.hxl.desktop.system.terminal.CommandConstant
import com.hxl.desktop.system.terminal.TerminalCommand

object ProcessUtils {
    fun stop(id: Int) {
        TerminalCommand.Builder()
            .add(CommandConstant.KILL9.format(id.toString()))
            .execute()
    }
}