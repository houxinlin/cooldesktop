package com.hxl.desktop.system.ssh

/**
 * 终端响应
 */
interface TerminalResponse {
    fun output(response: ByteArray)
}