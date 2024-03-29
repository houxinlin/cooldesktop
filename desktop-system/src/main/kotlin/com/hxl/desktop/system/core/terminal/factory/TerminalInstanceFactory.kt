package com.hxl.desktop.system.core.terminal.factory

import com.hxl.desktop.system.core.terminal.Terminal
import com.hxl.desktop.system.core.terminal.LinuxTerminal
import com.hxl.desktop.system.core.terminal.ServerConnectionInfoWrap
import java.util.concurrent.atomic.AtomicInteger

object TerminalInstanceFactory {
    /**
     * 线程id
     */
    private val threadId = AtomicInteger(0)
    private const val THREAD_NAME_PREFIX = "ssh-client-thread-"
    private fun createThread(linuxTerminal: LinuxTerminal): LinuxTerminal {
        val thread = Thread(linuxTerminal)
        thread.name = "$THREAD_NAME_PREFIX${threadId.addAndGet(1)}"
        thread.start()
        return linuxTerminal
    }

    /**
     * 创建新连接
     */
    fun getTerminal(info: ServerConnectionInfoWrap): Terminal {
        return createThread(LinuxTerminal(info))
    }
}