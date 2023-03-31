package com.hxl.desktop.system.core.command

import java.text.MessageFormat

enum class CommandConstant(var command: String) {
    SSH_KEYGEN("ssh-keygen -m PEM -b 2048 -f  {0} -q -N \"\""),
    JAR_RUN("nohup java {0} -jar {1} {2} >>{3}.out 2>&1 & "),
    KILL9("kill -9 {0}"),
    TAIL_F("tail -F -n 250 {0}"),
    FIND_PROCESS_LISTENER_PORT_BY_NETSTAT("netstat -tnlp | grep {0} | awk \'\''{print \$4}'\'\' | awk -F: \'\''{print \$NF}'\'\' | sort -u"),
    FIND_PROCESS_LISTENER_PORT_BY_LSOF("lsof -i -P -n  | grep {0} | grep LISTEN | awk \'\''{print \$9}'\'\' | cut -d \":\" -f 2 | sort -u");
    fun format(vararg param: String): String {
        return MessageFormat.format(command, *param)
    }
}