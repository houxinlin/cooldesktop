package com.hxl.desktop.file.service

import common.result.FileHandlerResult
import com.hxl.desktop.common.bean.FileAttribute
import common.bean.UploadInfo
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.FileSystemResource
import org.springframework.http.ResponseEntity
import org.springframework.scheduling.annotation.Async
import java.util.concurrent.Future

interface IFileService {
    fun listDirector(root: String): List<FileAttribute>;

    fun getFileIconByType(type: String): ByteArrayResource;

    fun deleteFile(path: String): FileHandlerResult;

    fun getImageThumbnail(path: String): ByteArrayResource;

    fun chunkUpload(uploadInfo: UploadInfo): FileHandlerResult

    fun fileMerge(path: String, name: String, inPath: String): FileHandlerResult;

    fun hasPermission(path: String): Boolean;

    fun fileCopy(path: String): Boolean

    fun fileRename(source: String, newName: String): FileHandlerResult

    fun fileCut(path: String): Boolean

    fun createFile(parent: String, name: String, type: String): FileHandlerResult

    fun getTextFileContent(path: String): FileHandlerResult

    fun setTextFileContent(path: String, content: String): FileHandlerResult

    @Async
    fun filePaste(path: String, taskId: String): Future<FileHandlerResult>

    @Async
    fun fileCompress(path: String, targetName: String, compressType: String,taskId: String): Future<FileHandlerResult>

    @Async
    fun fileDecompression(path: String,taskId: String): Future<FileHandlerResult>

    fun download(path: String): ResponseEntity<FileSystemResource>

}