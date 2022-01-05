package com.hxl.desktop.system.ssh

interface TerminalOutput {
    fun output(data: ByteArray)
}