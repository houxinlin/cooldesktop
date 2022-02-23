package com.hxl.desktop.system.ssh

/**
 * 终端连接信息
 */
class ServerConnectionInfo {
    var userName: String = "root"
    var host: String = ""
    var port: Int = 22
    var pass: String = ""
    lateinit var terminalResponse: TerminalResponse
}