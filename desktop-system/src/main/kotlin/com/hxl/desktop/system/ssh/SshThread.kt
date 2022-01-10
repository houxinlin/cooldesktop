package com.hxl.desktop.system.ssh

interface SshThread : Runnable {

    fun writeCommand(command: String);

    fun startTerminal();

    fun stopTerminal();

    fun setSize(col: Int, row: Int, w: Int, h: Int)
}