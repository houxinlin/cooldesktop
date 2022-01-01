package com.hxl.desktop.file.service

import com.hxl.desktop.common.result.FileHandlerResult
import com.hxl.desktop.common.bean.FileAttribute
import com.hxl.desktop.common.bean.UploadInfo
import org.springframework.core.io.ByteArrayResource
import org.springframework.scheduling.annotation.Async
import org.springframework.web.multipart.MultipartFile
import java.util.concurrent.Future

interface IFileService {
    fun listDirector(root: String): List<FileAttribute>;

    fun getFileIconByType(type: String): ByteArrayResource;

    fun deleteFile(path: String): FileHandlerResult;

    fun getImageThumbnail(path: String): ByteArrayResource;

    fun checkUploadFile(uploadInfo: UploadInfo): Boolean

    fun fileMerge(path: String, name: String, inPath: String): FileHandlerResult;

    fun hasPermission(path: String): Boolean;

    fun fileCopy(path: String): Boolean

    fun filePaste(path: String): FileHandlerResult

    fun fileRename(source: String, newName: String): FileHandlerResult

    fun fileCut(path: String): Boolean


    @Async
    fun fileCompress(path: String, targetName: String, compressType: String): Future<FileHandlerResult>

}