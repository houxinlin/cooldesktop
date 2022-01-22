package com.hxl.desktop.system.property

import com.hxl.desktop.system.ssh.SSHUserInfo
import com.hxl.desktop.system.ssh.SshServerInfo
import org.springframework.stereotype.Service

@Service
class SystemProperty {
    fun getSSHUserInfo(): SshServerInfo {
        return SshServerInfo().apply {
            userName="root"
            pass="hxl495594.."
            host="www.houxinlin.com"
        }
    }
}