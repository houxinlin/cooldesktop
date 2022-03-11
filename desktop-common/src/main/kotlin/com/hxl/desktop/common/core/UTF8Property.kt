package com.hxl.desktop.common.core

import java.nio.charset.Charset
import java.util.*

class UTF8Property : Properties() {
    override fun getProperty(key: String?): String {
        return super.getProperty(key).toByteArray(Charset.forName("ISO-8859-1")).decodeToString()
    }

}