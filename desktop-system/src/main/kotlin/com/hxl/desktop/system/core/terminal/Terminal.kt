package com.hxl.desktop.system.core.terminal

/**
 * 终端
 */
interface Terminal : Runnable {

    fun writeCommand(command: String)

    fun startTerminal()

    fun stopTerminal()

    fun setSize(col: Int, row: Int, w: Int, h: Int)
}