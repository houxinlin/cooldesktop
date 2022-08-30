package com.hxl.desktop.system.core.terminal

import com.hxl.desktop.common.kotlin.extent.toFile

class LinuxShell(private val path: String) {
    fun run(): String {
        val processBuilder = ProcessBuilder()
        if (path.toFile().isDirectory) return "错误的路径${path}"
        processBuilder.directory(path.toFile().parent.toFile())
        processBuilder.command("bash", path)
        val process = processBuilder.start()
        process.waitFor().run {
            if (this == 0) return process.inputStream.readBytes().decodeToString()
            return process.errorStream.readBytes().decodeToString()
        }
    }
}