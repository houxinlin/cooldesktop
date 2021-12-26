package com.hxl.desktop.file.service

import com.hxl.desktop.common.result.FileHandlerResult
import com.hxl.desktop.common.bean.FileAttribute
import com.hxl.desktop.common.bean.UploadInfo
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
    fun deleteFile(path: String): FileHandlerResult;


    fun getImageThumbnail(path: String): ByteArrayResource;


    fun checkUploadFile(uploadInfo: UploadInfo):Boolean

    fun fileMerge(path: String,  name: String,inPath:String): FileHandlerResult;
}