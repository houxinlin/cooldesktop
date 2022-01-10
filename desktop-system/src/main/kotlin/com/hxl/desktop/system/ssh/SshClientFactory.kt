package com.hxl.desktop.system.ssh

class SshClientFactory {
    fun createSshSshClient(
        userName: String,
        host: String,
        port: Int,
        pass: String,
        terminalOutput: TerminalOutput
    ): SshThread {
        var sshClient = SshClient(userName, host, port, pass, terminalOutput)
        Thread(sshClient).start()
        return sshClient
    }
}