package com.hxl.desktop.common.extent

import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.http.CacheControl
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import java.util.concurrent.TimeUnit


fun ByteArray.toHttpResponse(mediaType: MediaType): ResponseEntity<Resource> {
    val resource = ByteArrayResource(this)
    val header = HttpHeaders()
    return ResponseEntity.ok()
        .headers(header)
        .cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS))
        .contentType(mediaType)
        .contentLength(resource.contentLength())
        .body(resource);
}

