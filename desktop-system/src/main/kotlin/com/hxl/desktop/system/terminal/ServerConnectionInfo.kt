package com.hxl.desktop.system.terminal

/**
 * 终端连接信息
 */
class ServerConnectionInfo {
    var userName: String = ""
    var host: String = ""
    var port: Int = 22
    var pass: String = ""
    var publicKey: String = ""
    var privateKey: String = ""
    fun verification(): Boolean {
        return userName.isNotEmpty() && publicKey.isNotEmpty() && privateKey.isNotEmpty()
    }

    override fun toString(): String {
        return "ServerConnectionInfo(userName='$userName', host='$host', port=$port, pass='$pass', publicKey='$publicKey', privateKey='$privateKey')"
    }

}