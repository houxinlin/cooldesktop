package com.hxl.desktop.common.utils

import com.fasterxml.jackson.databind.ObjectMapper

object JSON {
    fun <T> parseObject(json: String, cls: Class<T>): T {
        var objectMapper = ObjectMapper()
        return objectMapper.readValue(json, cls)
    }
}