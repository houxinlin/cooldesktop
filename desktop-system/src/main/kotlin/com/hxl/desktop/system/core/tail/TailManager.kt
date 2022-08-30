package com.hxl.desktop.system.core.tail

import java.util.*

object TailManager {
    private val tailMap = mutableMapOf<String, Tail>()
    fun create(path: String, tailCallback: TailCallback): String {
        val uuid = UUID.randomUUID().toString()
        tailMap[uuid] = Tail(path, tailCallback).apply { this.begin() }
        return uuid
    }

    fun stop(uuid: String) {
        tailMap[uuid]?.stop()
    }

    fun stopAll() {
        tailMap.keys.forEach(this::stop)
    }
}