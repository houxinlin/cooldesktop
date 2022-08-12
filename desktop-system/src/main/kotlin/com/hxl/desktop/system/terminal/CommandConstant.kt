package com.hxl.desktop.system.terminal

import java.text.MessageFormat

enum class CommandConstant(var command: String) {
    SSH_KEYGEN("ssh-keygen -m PEM -b 2048 -f  {0} -q -N \"\""),
    JAR_RUN("nohup java -jar {0} {1} >>{2}.out 2>&1 & "),
    KILL9("kill -9 {0}"),
    TAIL_F("tail -F -n 250 {0}");
    fun format(vararg param: String): String {
        return MessageFormat.format(command, *param)
    }
}