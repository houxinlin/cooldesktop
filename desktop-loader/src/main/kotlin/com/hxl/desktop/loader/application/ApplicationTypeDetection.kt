package com.hxl.desktop.loader.application

import com.desktop.application.definition.application.Application
import com.hxl.fm.pk.FilePackage.readInt
import java.nio.ByteBuffer

object ApplicationTypeDetection {
    fun detection(data: ByteArray): Int {
        var applicationByteBuffer = ByteBuffer.wrap(data)
        if (applicationByteBuffer.readInt() == 495934) {
            return Application.WEB_MINI_APP
        }
        return 0
    }
}