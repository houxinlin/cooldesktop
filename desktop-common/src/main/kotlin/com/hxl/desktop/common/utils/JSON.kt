package com.hxl.desktop.common.utils

import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.ObjectMapper

object JSON {
    private val objectMapper = ObjectMapper()
    fun <T> parseObject(json: String, cls: Class<T>): T {

        return objectMapper.readValue(json, cls)
    }

    fun <T> parseList(json: String, clazz: Class<T>): MutableList<T>? {
        val javaType: JavaType =
            objectMapper.typeFactory.constructParametricType(ArrayList::class.java, clazz)
        return objectMapper.readValue<MutableList<T>?>(json, javaType)
    }
}