package com.hxl.desktop.system.terminal

import java.io.IOException

class TerminalCommand {
    private constructor()

    private fun execute(commandBuilder: Builder): String {
        try {
            val processBuilder = ProcessBuilder()
            processBuilder.command(commandBuilder.commands)
            val process = processBuilder.start()
            process.waitFor()
            return process.inputStream.readBytes().decodeToString()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        return ""
    }

    class Builder {
        var commands = mutableListOf<String>()

        init {
            commands.add("bash")
            commands.add("-c")
        }

        fun add(command: String): Builder {
            commands.add(command)
            return this
        }
        fun execute(): String {
            return TerminalCommand().execute(this)
        }
    }
}