package com.hxl.desktop.file.service

import com.hxl.desktop.common.result.FileHandlerResult
import com.hxl.desktop.common.bean.FileAttribute
import org.springframework.core.io.ByteArrayResource
import org.springframework.web.multipart.MultipartFile

interface IFileService {
    /**
     * list files
     */
    fun listDirector(root: String): List<FileAttribute>;

    /**
     * get file preview image
     */
    fun getFileIconByType(type: String): ByteArrayResource;

    /**
     * delete file and folder
     */
    fun deleteFile(path: String): String;


    fun getImageThumbnail(path: String): ByteArrayResource;


    fun checkUploadFile(chunkId:String,blob: Int, body: MultipartFile):Boolean

    fun fileMerge(path: String, size: Int, name: String,inPath:String): FileHandlerResult;
}