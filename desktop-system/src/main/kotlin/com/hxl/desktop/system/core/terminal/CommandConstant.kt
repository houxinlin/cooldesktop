package com.hxl.desktop.system.core.terminal

import java.text.MessageFormat

enum class CommandConstant(var command: String) {
    SSH_KEYGEN("ssh-keygen -m PEM -b 2048 -f  {0} -q -N \"\""),
    JAR_RUN("nohup java {0} -jar {1} {2} >>{3}.out 2>&1 & "),
    KILL9("kill -9 {0}"),
    TAIL_F("tail -F -n 250 {0}");
    fun format(vararg param: String): String {
        return MessageFormat.format(command, *param)
    }
}