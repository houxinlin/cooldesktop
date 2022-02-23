package com.hxl.desktop.system.property

import com.hxl.desktop.system.ssh.ServerConnectionInfo
import org.springframework.stereotype.Service

@Service
class SystemProperty {
    /**
     * 获取系统SSH连接信息
     */
    fun getSSHUserInfo(): ServerConnectionInfo {
        return ServerConnectionInfo().apply {
            userName = "root"
            pass = "hxl495594.."
            host = "www.houxinlin.com"
        }
    }
}