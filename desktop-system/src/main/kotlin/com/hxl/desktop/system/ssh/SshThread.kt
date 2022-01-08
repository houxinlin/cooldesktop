package com.hxl.desktop.system.ssh

interface SshThread : Runnable {

    fun writeCommand(command:String);

    fun startTerminal();

    fun stopTerminal();
}