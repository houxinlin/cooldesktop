package com.hxl.desktop.system.terminal

import com.hxl.desktop.common.core.Constant
import com.hxl.desktop.common.core.Directory
import com.hxl.desktop.system.sys.CoolDesktopSystem
import com.jcraft.jsch.ChannelShell
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.InputStream
import java.io.OutputStream
import java.nio.file.Paths


class LinuxTerminal(var serverConnectionWrap: ServerConnectionInfoWrap) : Terminal {
    companion object {
        private const val CONNECTION_TIMEOUT: Int = 1000 * 5
        private val CONNECTION_FAIL = "连接失败".toByteArray()
        val log: Logger = LoggerFactory.getLogger(LinuxTerminal::class.java)
    }

    private val jsch = JSch()
    private var session: Session? = null
    private var terminalInputStream: InputStream? = null
    private var terminalOutputStream: OutputStream? = null
    private var channelShell: ChannelShell? = null
    private var connectioned = false;
    private var offlineCommand = mutableListOf<String>()

    override fun setSize(col: Int, row: Int, w: Int, h: Int) {
        log.info("设置大小$col ${row}")
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
            log.info("连接成功")
            channelShell = session!!.openChannel("shell") as ChannelShell
            with(channelShell!!) {
                terminalInputStream = this.inputStream
                terminalOutputStream = this.outputStream
                connect(CONNECTION_TIMEOUT)
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
                serverConnectionWrap.terminalResponse.output(buffer.copyOfRange(0, i))
            }
        } catch (e: Exception) {
            serverConnectionWrap.terminalResponse.output(e.message!!.toByteArray())
        } finally {
            terminalInputStream?.close()
            terminalOutputStream?.close()
            stopTerminal()
        }
    }

    private fun initJsch(): Boolean {
        try {
            jsch.addIdentity(
                Paths.get(Directory.getSecureShellConfigDirectory(), CoolDesktopSystem.RSA_NAME).toString()
            )
            session = jsch.getSession(
                serverConnectionWrap.info.userName,
                serverConnectionWrap.info.host,
                serverConnectionWrap.info.port
            )
            if (session != null) {
                with(session!!) {
                    this.setConfig("StrictHostKeyChecking", "no")
                    this.connect(CONNECTION_TIMEOUT)
                    connectioned = true
                    return true
                }
            }
            //连接失败
            serverConnectionWrap.terminalResponse.output(CONNECTION_FAIL);
        } catch (e: Exception) {
            serverConnectionWrap.terminalResponse.output(Constant.StringConstant.SSH_CONNECTION_FAIL.toByteArray())
        }
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