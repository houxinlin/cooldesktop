package com.hxl.desktop.system.core.terminal

/**
 * 终端响应
 */
interface TerminalResponse {
    fun output(response: ByteArray)
}