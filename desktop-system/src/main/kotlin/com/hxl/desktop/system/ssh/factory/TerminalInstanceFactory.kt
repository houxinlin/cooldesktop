package com.hxl.desktop.system.ssh.factory

import com.hxl.desktop.system.ssh.Terminal
import com.hxl.desktop.system.ssh.LinuxTerminal
import com.hxl.desktop.system.ssh.ServerConnectionInfo
import java.util.concurrent.atomic.AtomicInteger

object TerminalInstanceFactory {
    /**
     * 线程id
     */
    var threadId = AtomicInteger(0)
    private const val THREAD_NAME_PREFIX = "ssh-client-thread-"
    private fun createThread(linuxTerminal: LinuxTerminal): LinuxTerminal {
        var thread = Thread(linuxTerminal)
        thread.name = "${THREAD_NAME_PREFIX}${threadId.addAndGet(1)}"
        thread.start()
        return linuxTerminal
    }

    /**
     * 创建新连接
     */
    fun getTerminal(info: ServerConnectionInfo): Terminal {
        var linuxTerminal = LinuxTerminal(info)
        return createThread(linuxTerminal)
    }
}