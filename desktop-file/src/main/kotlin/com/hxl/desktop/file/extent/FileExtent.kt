package com.hxl.desktop.file.extent

import com.hxl.desktop.common.bean.FileAttribute
import com.hxl.desktop.file.emun.FileType
import com.hxl.desktop.file.extent.FileExtent.logger
import com.hxl.desktop.file.utils.FileCompressUtils
import org.apache.tika.Tika
import org.slf4j.LoggerFactory
import org.springframework.core.io.FileSystemResource
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.PosixFileAttributeView

object FileExtent {
    var logger = LoggerFactory.getLogger(FileExtent::class.java)
}

fun File.canReadAndWrite(): Boolean {
    return this.canRead() && this.canWrite()
}

/**
 * 获取文件类型，主要返回是不是图片、文本、音乐、视频
 */
fun File.getFileTypeUseMagic(): String {
    if (!this.canRead()) {
        return "text"
    }
    try {
        var detect = createTika().detect(this)
        return detect.substring(0, detect.indexOf("/"))
    } catch (e: Exception) {
        logger.info("获取文件类型异常{},{}", e.message, this)
    }
    return "text"
}

fun createTika(): Tika {
    return Tika()
}


/**
 * 获取文件扩展名
 */
fun File.getFileSuffixValue(): String {
    if (this.isDirectory) {
        return FileType.FOLDER.typeName
    }
    var pointIndex = this.name.lastIndexOf(".")
    if (pointIndex >= 0) {
        var suffix = this.name.substring(pointIndex + 1)
        if (suffix.isNotEmpty()) {
            return suffix.lowercase();
        }
    }
    return FileType.NONE.typeName
}

/**
 * 获取文件类型
 */
fun File.getFileType(): String {
    if (this.isDirectory) {
        return FileType.FOLDER.typeName
    }
    return getFileTypeUseMagic()
}

/**
 * 文件大小
 */
fun File.getFileSize(): Long {
    if (this.isDirectory) {
        return -1;
    }
    return Files.size(this.toPath())
}

/**
 * 是否是文本文件
 */
fun File.isTextFile(): Boolean {
    if (this.isDirectory) {
        return false
    }

    var fileType = this.getFileTypeUseMagic()
    return fileType.startsWith("text")
}

/**
 * 获取Mime类型
 */
fun File.getMimeType(): String {
    if (!this.canRead() || this.length() == 0L) {
        return "text/none"
    }
    if (this.isDirectory) {
        return FileType.FOLDER.typeName
    }
    return createTika().detect(this);
}

/**
 * 获取文件属性
 */

fun File.getAttribute(): FileAttribute {
    /**
     * 在jdk17这里有点问题，jackson无法序列化他，原因不知
     */
    var readAttributes = Files.getFileAttributeView(this.toPath(), PosixFileAttributeView::class.java).readAttributes()
    return FileAttribute(
        this.toString(),
        this.getFileType(),
        this.getFileSize(),
        this.name,
        this.getFileSuffixValue(),
        readAttributes.creationTime().toMillis(),
        readAttributes.lastAccessTime().toMillis(),
        readAttributes.lastModifiedTime().toMillis(),
        Files.getOwner(this.toPath()).name,
        isTextFile(),
        getMimeType(),
        !readAttributes.isDirectory
    )
}

/**
 * 压缩
 */
fun File.compress(type: String, targetName: String) {
    FileCompressUtils.getCompressByType(type).compress(this.toString(), targetName)
}

/**
 * 解压
 */
fun File.decompression() {
    FileCompressUtils.getCompressByType(FileCompressUtils.getFileType(this.toString()))
        .decompression(this.toString())
}

fun File.toHttpResponse(): ResponseEntity<FileSystemResource> {
    val header = HttpHeaders()
    header.set("Content-Disposition", "attachment; filename=" + this.name)
    return ResponseEntity.ok()
        .headers(header)
        .contentLength(this.length())
        .body(FileSystemResource(this));
}