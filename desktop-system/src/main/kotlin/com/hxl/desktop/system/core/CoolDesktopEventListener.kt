package com.hxl.desktop.system.core

import com.hxl.desktop.common.core.Constant
import com.hxl.desktop.common.extent.toFile
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import java.awt.Event
import java.io.File
import java.util.function.Consumer

@Component
class CoolDesktopEventListener {
    private val log: Logger = LoggerFactory.getLogger(CoolDesktopEventListener::class.java.name)

    @Autowired
    lateinit var webSocketSender: WebSocketSender
    val handlerMap = mutableMapOf(
        1 to Consumer<Event> { openDirector(it) }
    )

    fun createDelayMessageToOpenDirectory(data: String): String {
        return WebSocketMessageBuilder.Builder()
            .applySubject(Constant.WebSocketSubjectNameConstant.OPEN_DIRECTORY)
            .addItem("data", data)
            .build()
    }

    fun openDirector(event: Event) {
        var file = File(event.arg.toString())
        if (file.isFile) file = file.parent.toFile()
        webSocketSender.sendForDelay(createDelayMessageToOpenDirectory(file.toString()), "", 0)
    }

    @EventListener
    fun event(event: Event) {
        log.info(event.toString())
        handlerMap[event.id]?.accept(event)
    }
}