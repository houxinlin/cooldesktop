package com.hxl.desktop.common.kotlin.extent

import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.http.CacheControl
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import java.util.concurrent.TimeUnit


fun ByteArray.toHttpResponse(mediaType: MediaType=MediaType.APPLICATION_OCTET_STREAM): ResponseEntity<Resource> {
    val resource = ByteArrayResource(this)
    val header = HttpHeaders()
    return ResponseEntity.ok()
        .headers(header)
        .cacheControl(CacheControl.noCache())
        .contentType(mediaType)
        .contentLength(resource.contentLength())
        .body(resource);
}

