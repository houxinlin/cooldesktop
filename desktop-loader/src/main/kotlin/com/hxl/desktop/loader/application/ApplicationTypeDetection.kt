package com.hxl.desktop.loader.application

import com.desktop.application.definition.application.Application
import com.hxl.fm.pk.FilePackage.readInt
import java.nio.ByteBuffer

object ApplicationTypeDetection {
    fun detection(data: ByteBuffer): Int {
        data.position(0)
        if (data.readInt() == 495934) {
            return Application.WEB_MINI_APP
        }
        data.position(0)
        if (data.readInt() == 1347093252) {
            return Application.EASY_APP
        }
        return 0
    }

    fun detection(data: ByteArray): Int {
        return detection(ByteBuffer.wrap(data))
    }
}