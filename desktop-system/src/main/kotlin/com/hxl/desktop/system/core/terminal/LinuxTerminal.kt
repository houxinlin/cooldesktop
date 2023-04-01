package com.hxl.desktop.system.core.terminal

import com.pty4j.PtyProcess
import com.pty4j.PtyProcessBuilder
import com.pty4j.WinSize
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.InputStream
import java.io.OutputStream
import java.util.concurrent.CountDownLatch
import java.util.concurrent.LinkedBlockingQueue

class LinuxTerminal(private var serverConnectionWrap: ServerConnectionInfoWrap) : Terminal {
    companion object {
        private val log: Logger = LoggerFactory.getLogger(LinuxTerminal::class.java)
        private const val INIT_ECHO = "\u001B[1;34m欢迎来到CoolDesktop终端\u001B[00m  \n\r" +
                "\u001B[1;34m在这里您可以尽情得操作\u001B[00m\r\n"
    }

    @Volatile
    private var terminalInputStream: InputStream? = null

    @Volatile
    private var terminalOutputStream: OutputStream? = null
    private val waitInit = CountDownLatch(1);

    @Volatile
    private var connectioned = false
    private var commandQueue: LinkedBlockingQueue<String> = LinkedBlockingQueue<String>()
    private var commandConsumeThread: Thread? = null
    private lateinit var ptyProcessBuilder: PtyProcessBuilder
    private lateinit var process: PtyProcess
    private var terminalHome = ""


    override fun setSize(col: Int, row: Int, w: Int, h: Int) {
        log.info("终端大小发生改变，新的大小为$col ${row}")
        process.winSize = WinSize(col, row, w, h)
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
        if (command.startsWith("INIT_HOME_TERMINAL:")) {
            terminalHome = command.substring(19)
            waitInit.countDown()
            return
        }
        commandQueue.offer(command)

    }

    override fun startTerminal() {
        waitInit.await()
        println("startTerminal")
        val home = if (terminalHome.isBlank()) System.getProperty("user.home") else terminalHome
        ptyProcessBuilder = PtyProcessBuilder()
            .setDirectory(home)
            .setCommand(arrayOf("/bin/bash"))
        process = ptyProcessBuilder.start()
        serverConnectionWrap.terminalResponse.output(INIT_ECHO.toByteArray())
        readTerminalData()
    }


    private fun startCommandConsumeThread() {
        commandConsumeThread = Thread() {
            try {
                while (true) {
                    val command = commandQueue.take()
                    if (command.startsWith("setSize")) {
                        handlerSystemCommand(command)
                        continue
                    }
                    terminalOutputStream?.write(((command).toByteArray()))
                    terminalOutputStream?.flush()
                }
            } catch (_: Exception) {
            }
        }
        commandConsumeThread!!.start()
    }

    private fun readTerminalData() {
        try {
            terminalInputStream = process.inputStream
            terminalOutputStream = process.outputStream
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

    override fun stopTerminal() {
        commandQueue.clear()
        commandConsumeThread?.interrupt()
        connectioned = false
        if (::process.isInitialized){
            process.destroyForcibly()
        }
    }
}