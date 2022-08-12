package com.hxl.desktop.system.tail

import com.hxl.desktop.system.terminal.CommandConstant
import org.slf4j.LoggerFactory
import java.lang.Exception

class Tail(private val path: String, private val tailCallback: TailCallback) : Runnable {
    companion object {
        private val log = LoggerFactory.getLogger(Tail::class.java)
    }

    private var process: Process? = null
    fun begin() {
        Thread(this).start()
    }

    override fun run() {
        log.info("日志追踪 {}", path)
        val processBuilder = ProcessBuilder()
        processBuilder.command("bash", "-c", CommandConstant.TAIL_F.format(path))
        process = processBuilder.start()
        val bufferedReader = process!!.inputStream.bufferedReader()
        try {
            while (true) { tailCallback.line(bufferedReader.readLine()) }
        } catch (ex: Exception) {
            log.info("日志追踪退出 {}", path)
        }
    }

    fun stop() {
        process?.destroy()
    }
}