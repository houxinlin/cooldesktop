package com.hxl.desktop.web.util.http.client

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.type.CollectionType
import okhttp3.Call
import kotlin.jvm.Throws
import kotlin.math.log


class ListMapResponse(private val call: Call) {
    companion object {
        val objectMapper = ObjectMapper()
    }

    @Throws(Exception::class)
    fun getValue(): List<Map<String, Any>> {
        val response = call.execute().body?.string()
        val listType: CollectionType = objectMapper.typeFactory.constructCollectionType(ArrayList::class.java,
            MutableMap::class.java)
        return objectMapper.readValue(response, listType)

    }
}