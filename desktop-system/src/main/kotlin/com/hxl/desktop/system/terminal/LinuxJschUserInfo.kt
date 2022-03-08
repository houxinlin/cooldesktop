package com.hxl.desktop.system.terminal

import com.jcraft.jsch.UserInfo

class LinuxJschUserInfo : UserInfo {
    override fun getPassphrase(): String? {
        println("getPassphrase")
        return null
    }

    override fun getPassword(): String? {
        println("getPassword")
        return null
    }

    override fun promptPassword(s: String): Boolean {
        println("promptPassword:$s")
        return false
    }

    override fun promptPassphrase(s: String): Boolean {
        println("promptPassphrase:$s")
        return false
    }

    override fun promptYesNo(s: String): Boolean {
        println("promptYesNo:$s")
        return true //notice here!
    }

    override fun showMessage(s: String) {
        println("showMessage:$s")
    }
}