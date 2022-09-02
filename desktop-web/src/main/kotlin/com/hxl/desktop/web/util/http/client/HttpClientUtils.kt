package com.hxl.desktop.web.util.http.client

import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

object HttpClientUtils {
    private  val  okHttpClient = OkHttpClient.Builder()
        .connectTimeout(5,TimeUnit.SECONDS)
        .readTimeout(5,TimeUnit.SECONDS)
        .build()
    fun createGetRequest(url:String,param:Map<String,Any> = mutableMapOf()): Call {
        var newUrl =url
        if (param.isNotEmpty()){
            if (!newUrl.endsWith("?")) newUrl="$newUrl?"
            param.forEach { (k, v) -> newUrl="$newUrl$k=$v&" }
        }
        val req = Request.Builder()
            .get()
            .url(newUrl)
            .build()
      return  okHttpClient.newCall(req)
    }

}