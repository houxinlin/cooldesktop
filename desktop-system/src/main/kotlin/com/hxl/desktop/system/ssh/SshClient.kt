package com.hxl.desktop.system.ssh

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

    private val jsch = JSch()
    private var session: Session? = null;

    var inputStream: InputStream? = null
    var outputStream: OutputStream? = null

    override fun writeCommand(command: String) {
        println(command)
        outputStream?.write(((command ).toByteArray()))
        outputStream?.flush()
    }

    override fun startTerminal() {
        initJsch()
        if (session != null) {
            readTerminalData();
            return
        }
        terminalOutput.output("连接失败".toByteArray())
    }

    override fun stopTerminal() {
        session?.disconnect()
        terminalOutput.output("终端关闭".toByteArray())
    }

    override fun run() {
        startTerminal()
    }

    private fun readTerminalData() {
        try {
            var channel = session!!.openChannel("shell")
            inputStream = channel.getInputStream()
            outputStream = channel.getOutputStream()
            channel.connect(10000)
            val buffer = ByteArray(1024)
            var i = 0
            while (inputStream!!.read(buffer).also { i = it } != -1) {
                terminalOutput.output(Arrays.copyOfRange(buffer, 0, i))
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

    companion object {
        fun createSshClient(
            userName: String,
            host: String,
            port: Int,
            pass: String,
            terminalOutput: TerminalOutput
        ): SshClient {
            return SshClient(userName, host, port, pass, terminalOutput)
        }
    }
}