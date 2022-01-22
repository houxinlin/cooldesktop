package com.hxl.desktop.system.ssh

import java.util.concurrent.atomic.AtomicInteger

class SshClientFactory {
    var threadId = AtomicInteger(0)
    private fun createThread(sshClient: SshClient): SshClient {
        var thread = Thread(sshClient)
        thread.name = "ssh-client-thread-" + threadId.addAndGet(1)
        thread.start()
        return sshClient
    }

    fun createSshSshClient(info: SshServerInfo): SshThread {
        var sshClient = SshClient(info.userName, info.host, info.port, info.pass, info.terminalOutput)
        return createThread(sshClient)
    }
}