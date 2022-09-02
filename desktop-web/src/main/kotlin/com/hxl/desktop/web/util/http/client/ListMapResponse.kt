package com.hxl.desktop.web.util.http.client

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.type.CollectionType
import okhttp3.Call
import kotlin.math.log


class ListMapResponse(private val call:  Call) {
    companion object{
         val objectMapper =ObjectMapper()
    }
    fun getValue():List<Map<String,Any>>{
        try {
            val response = call.execute().body?.string()
            val listType: CollectionType = objectMapper.typeFactory.constructCollectionType(ArrayList::class.java,
                MutableMap::class.java)
            return objectMapper.readValue(response,listType)
        }catch (e:Exception){
            e.printStackTrace()
        }
        return emptyList()
    }
}