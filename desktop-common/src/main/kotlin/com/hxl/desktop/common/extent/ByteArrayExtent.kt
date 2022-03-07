package com.hxl.desktop.common.extent

import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity

class ByteArrayExtent
fun ByteArray.toHttpResponse():ResponseEntity<Resource>{
    val resource = ByteArrayResource(this)
    val header = HttpHeaders()
    return ResponseEntity.ok()
        .headers(header)
        .contentLength(resource.contentLength())
        .body(resource);
}