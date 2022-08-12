package com.hxl.desktop.system.terminal

/**
 * 终端响应
 */
interface TerminalResponse {
    fun output(response: ByteArray)
}