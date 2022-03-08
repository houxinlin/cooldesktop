package com.hxl.desktop.system.terminal

import java.text.MessageFormat

enum class CommandConstant(var command: String) {
    SSH_KEYGEN("ssh-keygen -m PEM -b 2048 -f  {0} -q -N \"\"");

    fun format(vararg param: String): String {
        return MessageFormat.format(command, *param)
    }
}