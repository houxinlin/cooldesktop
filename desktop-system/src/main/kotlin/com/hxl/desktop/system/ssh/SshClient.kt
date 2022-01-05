package com.hxl.desktop.system.ssh

import com.jcraft.jsch.Channel
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import java.util.*


class SshClient(
    var userName: String,
    var host: String,
    var port: Int,
    var pass: String
) : Thread() {

    private val jsch = JSch()
    private var session: Session? = null;
    private lateinit var terminalOutput: TerminalOutput

    override fun run() {
        super.run()

    }
    init {
        initJsch()
        if (session != null) {
            readTerminalData();
        }
    }

    private fun readTerminalData() {
        try {
            var channel = session!!.openChannel("shell")
            var mSSHInputStream = channel.getInputStream()
            var mSSHOutputStream = channel.getOutputStream()
            channel.connect(10000)
            val buffer = ByteArray(1024)
            var i = 0
            while (mSSHInputStream.read(buffer).also { i = it } != -1) {
                println(String(Arrays.copyOfRange(buffer, 0, i)))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
        }
    }

    private fun initJsch() {
        session = jsch.getSession(userName, host, port)
        session?.apply {
            setPassword(pass)
            userInfo = SSHUserInfo()
            connect(2000)
        }
    }

    companion object {
        fun createSshClient(userName: String, host: String, port: Int, pass: String): SshClient {
            return SshClient(userName, host, port, pass)
        }
    }
}