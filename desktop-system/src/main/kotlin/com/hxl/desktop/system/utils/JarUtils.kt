package com.hxl.desktop.system.utils

import com.hxl.desktop.common.kotlin.extent.toFile
import com.hxl.desktop.system.terminal.CommandConstant
import com.hxl.desktop.system.terminal.TerminalCommand
import com.sun.tools.attach.VirtualMachine
import com.sun.tools.attach.VirtualMachineDescriptor
import java.util.concurrent.TimeUnit
import java.util.stream.Collectors

object JarUtils {
    fun getJavaProcess(): List<VirtualMachineDescriptor> {
        return VirtualMachine.list()
    }


    fun isRun(path: String, exclude: List<Int>, waitSecond: Long): Boolean {
        for (i in 1 until waitSecond) {
            if (getJavaProcess().stream().filter { it.displayName().startsWith(path) && !exclude.contains(it.id().toInt()) }.count() >= 1) return true
            TimeUnit.SECONDS.sleep(i)
        }
        return false
    }

    fun getProcessIds(path: String): List<Int> {
        return getJavaProcess().stream().filter { it.displayName().startsWith(path) }.map { it.id().toInt() }
            .collect(Collectors.toList())
    }

    fun run(path: String, arg: String) {
        TerminalCommand.Builder()
            .setWorkHome(path.toFile().parent)
            .add(CommandConstant.JAR_RUN.format(path, arg,"log")).execute()
    }

    fun stopJar(path: String) {
        getProcessIds(path).stream().forEach(ProcessUtils::stop)
    }
}