package com.hxl.desktop.system.ssh

import com.jcraft.jsch.ChannelShell
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import java.io.InputStream
import java.io.OutputStream


class LinuxTerminal(var serverConnectionInfo: ServerConnectionInfo) : Terminal {
    companion object {
        private const val CONNECTION_TIMEROUT: Int = 1000 * 5
        private val CONNECTION_FAIL = "连接失败".toByteArray()
    }

    private val jsch = JSch()
    private var session: Session? = null
    private var terminalInputStream: InputStream? = null
    private var terminalOutputStream: OutputStream? = null
    private var channelShell: ChannelShell? = null
    private var connectioned = false;
    private var offlineCommand = mutableListOf<String>()

    override fun setSize(col: Int, row: Int, w: Int, h: Int) {
        channelShell?.setPty(true)
        channelShell?.setPtySize(col, row, w, h)
    }

    override fun run() {
        startTerminal()
    }

    private fun handlerSystemCommand(command: String) {
        var value = command.substring(7)
        var col = value.substring(0, 4).toInt()
        var row = value.substring(4, 8).toInt()
        var w = value.substring(8, 12).toInt()
        var h = value.substring(12, 16).toInt()
        setSize(col, row, w, h)
    }

    override fun writeCommand(command: String) {
        if (terminalOutputStream == null || !connectioned) {
            offlineCommand.add(command)
            return
        }
        if (command.startsWith("setSize")) {
            handlerSystemCommand(command)
            return
        }
        terminalOutputStream?.write(((command).toByteArray()))
        terminalOutputStream?.flush()
    }

    override fun startTerminal() {
        if (initJsch()) readTerminalData()
    }


    private fun readTerminalData() {
        try {
            channelShell = session!!.openChannel("shell") as ChannelShell
            with(channelShell!!) {
                terminalInputStream = this.inputStream
                terminalOutputStream = this.outputStream
                connect(CONNECTION_TIMEROUT)
            }
            /**
             * 离线命令
             */
            if (offlineCommand.size != 0) {
                synchronized(offlineCommand) {
                    offlineCommand.forEach(this::writeCommand)
                }
            }
            val buffer = ByteArray(1024)
            var i = 0
            while (terminalInputStream!!.read(buffer).also { i = it } != -1) {
                serverConnectionInfo.terminalResponse.output(buffer.copyOfRange(0, i))
            }
        } catch (e: Exception) {
            serverConnectionInfo.terminalResponse.output(e.message!!.toByteArray())
        } finally {
            terminalInputStream?.close()
            terminalOutputStream?.close()
            stopTerminal()
        }
    }

    private fun initJsch(): Boolean {
        session = jsch.getSession(serverConnectionInfo.userName, serverConnectionInfo.host, serverConnectionInfo.port)
        if (session != null) {
            with(session!!) {
                this.setConfig("userauth.gssapi-with-mic", "no")
                this.setConfig("StrictHostKeyChecking", "no");
                this.setPassword(serverConnectionInfo.pass)
                this.userInfo = LinuxJschUserInfo()
                this.connect(CONNECTION_TIMEROUT)
                connectioned = true
                return true
            }
        }
        serverConnectionInfo.terminalResponse.output(CONNECTION_FAIL);
        return false;
    }

    override fun stopTerminal() {
        offlineCommand.clear()
        session?.disconnect()
        session = null
        channelShell = null
        connectioned = false
    }

}