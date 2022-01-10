package com.hxl.desktop.system.ssh

import com.jcraft.jsch.ChannelShell
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import java.io.InputStream
import java.io.OutputStream
import java.util.*


class SshClient(
    var userName: String,
    var host: String,
    var port: Int,
    var pass: String,
    var terminalOutput: TerminalOutput
) : SshThread {
    private val timout: Int = 1000 * 5

    private val jsch = JSch()
    private var session: Session? = null;

    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null
    private var offlineCommand= mutableListOf<String>()
    private var channelShell: ChannelShell? = null;

    override fun setSize(col: Int, row: Int, w: Int, h: Int) {
        channelShell?.setPty(true)
        channelShell?.setPtySize(col, row, w, h)
    }

    override fun run() {
        startTerminal()
    }

    override fun writeCommand(command: String) {
        if (outputStream==null){
            offlineCommand.add(command)
            return
        }
        if (command.startsWith("setSize")) {
            var value = command.substring(7)
            var col = value.substring(0, 4).toInt()
            var row = value.substring(4, 8).toInt()
            var w = value.substring(8, 12).toInt()
            var h = value.substring(12, 16).toInt()
            setSize(col, row, w, h)
            return
        }
        outputStream?.write(((command).toByteArray()))
        outputStream?.flush()
    }

    override fun startTerminal() {
        initJsch()
        readTerminalData();

    }

    override fun stopTerminal() {
        session?.disconnect()
        terminalOutput.output("终端关闭".toByteArray())
    }


    private fun readTerminalData() {
        try {
            if (session == null) {
                terminalOutput.output("连接失败".toByteArray())
                return
            }

            channelShell = session!!.openChannel("shell") as ChannelShell
            inputStream = channelShell!!.inputStream
            outputStream = channelShell!!.outputStream
            channelShell!!.connect(timout)
            if (offlineCommand.size!=0){
                for (s in offlineCommand) {
                    writeCommand(s)
                }
            }
            val buffer = ByteArray(1024)
            var i = 0
            while (inputStream!!.read(buffer).also { i = it } != -1) {
                terminalOutput.output(buffer.copyOfRange(0, i))
            }
        } catch (e: Exception) {
            terminalOutput.output(e.message!!.toByteArray())
        } finally {
            inputStream?.close()
            outputStream?.close()
        }
    }

    private fun initJsch() {
        session = jsch.getSession(userName, host, port)
        session?.setConfig("userauth.gssapi-with-mic", "no")
        session?.setConfig("StrictHostKeyChecking", "no");
        session?.apply {
            setPassword(pass)
            userInfo = SSHUserInfo()
            connect(2000)
        }
    }

}