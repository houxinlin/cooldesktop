package com.hxl.desktop.system.ssh

class SshServerInfo {
    var userName: String = ""
    var host: String = ""
    var port: Int = 22
    var pass: String = ""
    lateinit var terminalOutput: TerminalOutput
}