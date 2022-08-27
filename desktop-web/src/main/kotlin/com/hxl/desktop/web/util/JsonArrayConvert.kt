package com.hxl.desktop.web.util

import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.ObjectMapper
import com.hxl.desktop.common.kotlin.extent.toFile
import com.hxl.desktop.file.bean.FileAttribute
import com.hxl.desktop.file.extent.getAttribute
import org.springframework.util.StringUtils
import java.util.function.Function
import java.util.stream.Collectors

/**
 * Json Array到 List<FileAttribute> 的转换
 */
class JsonArrayConvert : Function<String, MutableList<FileAttribute>> {
    override fun apply(it: String): MutableList<FileAttribute> {
        if (!StringUtils.hasLength(it)) return mutableListOf()
        val objectMapper = ObjectMapper()
        val javaType: JavaType =
            objectMapper.typeFactory.constructParametricType(ArrayList::class.java, String::class.java)
        return objectMapper
            .readValue<List<String>?>(it, javaType)
            .stream()
            .map {
                if (it.toFile().exists()) return@map it.toFile().getAttribute()
                return@map null
            }.filter { it != null }
            .collect(Collectors.toList())
    }
}