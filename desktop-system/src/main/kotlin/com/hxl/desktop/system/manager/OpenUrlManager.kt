package com.hxl.desktop.system.manager

import com.hxl.desktop.common.core.Constant
import com.hxl.desktop.common.core.Directory
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import kotlin.io.path.notExists


object OpenUrlManager {
    fun register(url: String): String {
        if (url.isBlank()) return Constant.StringConstant.CANNOT_BLANK
        if (url == "/") return Constant.StringConstant.NOT_SUPPORT_PARAMETER

        val newUrl = if (url.startsWith("/")) url else "/${url}"
        if (getOpenUrl().contains(newUrl)) return Constant.StringConstant.DUPLICATE
        val oldOpenPath = Paths.get(Directory.getOpenUrlDirectory(), Constant.FileName.OPEN_URL)
        var data = StringBuffer(newUrl).append("\r").toString()
        Files.write(oldOpenPath, data.toByteArray(), StandardOpenOption.CREATE, StandardOpenOption.APPEND)
        return Constant.StringConstant.OK
    }

    fun unregister(url: String) {
        val oldOpenPath = Paths.get(Directory.getOpenUrlDirectory(), Constant.FileName.OPEN_URL)
        if (oldOpenPath.notExists()) return

        val allOpenURLs = oldOpenPath.toFile().readLines()
        var stringBuffer = StringBuffer()
        allOpenURLs.filterNot { it == url }.forEach { stringBuffer.append(it);stringBuffer.append("\r\n") }

        oldOpenPath.toFile().writeText(stringBuffer.toString())
    }

    fun getOpenUrl(): MutableList<String> {
        val openUrlPath = Paths.get(Directory.getOpenUrlDirectory(), Constant.FileName.OPEN_URL)
        if (openUrlPath.notExists()) mutableListOf<String>()
        return Files.readAllLines(openUrlPath)
    }
}