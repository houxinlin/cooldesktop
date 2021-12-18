package com.hxl.desktop.file.service

import com.hxl.desktop.file.bean.FileAttribute
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ByteArrayResource
import org.springframework.stereotype.Service
interface IFileService {
    /**
     * list files
     */
    fun listDirector(root:String): List<FileAttribute>;

    /**
     * get file preview image
     */
    fun getFileIcon(path:String): ByteArrayResource;
}