package com.hxl.desktop.common.bean

import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile

data class UploadInfo(var chunkId: String,
                      var target: String,
                      var total: Long,
                      var fileName: String,
                      var blobId: Int,
                      var fileBinary: MultipartFile)
