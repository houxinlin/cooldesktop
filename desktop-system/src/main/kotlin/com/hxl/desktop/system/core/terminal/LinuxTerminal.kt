package com.hxl.desktop.system.core.terminal

import com.hxl.desktop.common.core.Constant
import com.hxl.desktop.common.core.Directory
import com.hxl.desktop.common.kotlin.extent.commandExist
import com.hxl.desktop.system.core.command.CommandConstant
import com.hxl.desktop.system.core.command.TerminalCommand
import com.hxl.desktop.system.core.sys.CoolDesktopSystem
import com.jcraft.jsch.ChannelShell
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.InputStream
import java.io.OutputStream
import java.nio.file.Paths
import java.util.concurrent.LinkedBlockingQueue
import java.util.stream.Collectors


class LinuxTerminal(private var serverConnectionWrap: ServerConnectionInfoWrap) : Terminal {
    companion object {
        private const val CONNECTION_TIMEOUT: Int = 1000 * 5
        private val CONNECTION_FAIL = "连接失败".toByteArray()
        private val log: Logger = LoggerFactory.getLogger(LinuxTerminal::class.java)
    }

    private val jsch = JSch()

    private var session: Session? = null

    @Volatile
    private var terminalInputStream: InputStream? = null

    @Volatile
    private var terminalOutputStream: OutputStream? = null

    @Volatile
    private var connectioned = false

    private var channelShell: ChannelShell? = null

    private var commandQueue: LinkedBlockingQueue<String> = LinkedBlockingQueue<String>()

    private var commandConsumeThread: Thread? = null
    override fun setSize(col: Int, row: Int, w: Int, h: Int) {
        log.info("终端大小发生改变，新的大小为$col ${row}")
        channelShell?.setPty(true)
        channelShell?.setPtySize(col, row, w, h)
    }

    override fun run() {
        startTerminal()
    }

    private fun handlerSystemCommand(command: String) {
        val value = command.substring(7)
        val col = value.substring(0, 4).toInt()
        val row = value.substring(4, 8).toInt()
        val w = value.substring(8, 12).toInt()
        val h = value.substring(12, 16).toInt()
        setSize(col, row, w, h)
    }


    override fun writeCommand(command: String) {
        commandQueue.offer(command)

    }

    override fun startTerminal() {
        if (initJsch()) readTerminalData()
    }


    private fun doHandlerCommand(command: String) {
        if (command.startsWith("setSize")) {
            handlerSystemCommand(command)
            return
        }
        terminalOutputStream?.write(((command).toByteArray()))
        terminalOutputStream?.flush()
    }

    private fun startCommandConsumeThread() {
        commandConsumeThread = Thread() {
            try {
                while (true) {
                    doHandlerCommand(commandQueue.take())
                }
            } catch (e: Exception) {
            }
        }
        commandConsumeThread!!.start()
    }

    private fun readTerminalData() {
        try {
            channelShell = session!!.openChannel("shell") as ChannelShell
            with(channelShell!!) {
                terminalInputStream = this.inputStream
                terminalOutputStream = this.outputStream
                connect(CONNECTION_TIMEOUT)
                connectioned = true
            }
            startCommandConsumeThread()

            val buffer = ByteArray(1024)
            var i = 0
            while (terminalInputStream!!.read(buffer).also { i = it } != -1) {
                serverConnectionWrap.terminalResponse.output(buffer.copyOfRange(0, i))
            }
        } catch (e: Exception) {
            log.info(e.message)
            serverConnectionWrap.terminalResponse.output(e.message!!.toByteArray())
        } finally {
            terminalInputStream?.close()
            terminalOutputStream?.close()
            stopTerminal()
        }
    }

    private fun initJsch(): Boolean {
        try {
            jsch.addIdentity(Paths.get(Directory.getSecureShellConfigDirectory(), CoolDesktopSystem.RSA_NAME).toString())

            //尝试推测ssh服务
            for (port in getSshdPorts()) {
                session = jsch.getSession(serverConnectionWrap.info.userName, serverConnectionWrap.info.host, port)
                if (session != null) {
                    with(session!!) {
                        this.setConfig("StrictHostKeyChecking", "no")
                        this.connect(CONNECTION_TIMEOUT)
                        return true
                    }
                }
            }
            //连接失败
            serverConnectionWrap.terminalResponse.output(CONNECTION_FAIL);
        } catch (e: Exception) {
            log.info(e.message)
            serverConnectionWrap.terminalResponse.output((e.message + ":" + Constant.StringConstant.SSH_CONNECTION_FAIL).toByteArray())
        }
        return false
    }

    override fun stopTerminal() {
        commandQueue.clear()
        commandConsumeThread?.interrupt()
        session?.disconnect()
        session = null
        channelShell = null
        connectioned = false
    }

    fun getSshdPorts(): List<Int> {
        val port: String = if ("lsof".commandExist()) {
            TerminalCommand.Builder()
                .add(CommandConstant.FIND_PROCESS_LISTENER_PORT_BY_LSOF.format("sshd"))
                .execute()
        } else {
            TerminalCommand.Builder()
                .add(CommandConstant.FIND_PROCESS_LISTENER_PORT_BY_NETSTAT.format("sshd"))
                .execute()
        }
        return port.split("\n")
            .stream()
            .map { if (it.isNotBlank()) it.toIntOrNull() else null }.filter { it != null }
            .map { it!!.toInt() }
            .collect(Collectors.toList())
    }
}