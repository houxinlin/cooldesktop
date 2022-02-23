package com.hxl.desktop.file.extent

import common.bean.FileAttribute
import com.hxl.desktop.file.emun.FileType
import com.hxl.desktop.file.extent.FileExtent.MAGIC_MAX_SIZE
import com.hxl.desktop.file.utils.FileCompressUtils
import com.hxl.desktop.file.utils.FileTypeRegister
import net.sf.jmimemagic.Magic
import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributeView

object FileExtent {
    const val MAGIC_MAX_SIZE: Int = 1024 * 1024 * 5;
}


/**
 * 获取文件类型，主要返回是不是图片、文本、音乐、视频
 */
fun File.getFileTypeUseMagic(): String {
    /**
     * 如果Magic检测不出来，直接返回是text
     */
    try {
        var mathch = Magic.getMagicMatch(this, true)
        mathch?.let {
            var mimeType = mathch.mimeType
            if (mimeType.startsWith("image")) {
                return FileType.IMAGE.typeName
            }
            if (mimeType.startsWith("text")) {
                return FileType.TEXT.typeName
            }
            if (mimeType.startsWith("application")) {
                return FileType.APPLICATION.typeName
            }
        }
        return FileType.TEXT.typeName
    } catch (e: Exception) {
        return FileType.TEXT.typeName
    }
}

/**
 * 获取文件类型，主要返回是不是图片、文本、音乐、视频
 */
fun File.getFileTypeUseSuffix(): String {
    var fileSuffixValue = getFileSuffixValue()
    if (fileSuffixValue == FileType.NONE.typeName) {
        return FileType.NONE.typeName
    }
    return FileTypeRegister.getFileTypeBySuffix(fileSuffixValue)
}


/**
 * 获取文件扩展名
 */
fun File.getFileSuffixValue(): String {
    if (this.isDirectory) {
        return FileType.FOLDER.typeName
    }
    var suffix = this.name.substring(this.name.lastIndexOf(".") + 1)
    if (suffix.isNotEmpty()) {
        return suffix.lowercase();
    }
    return FileType.NONE.typeName
}

/**
 * 获取文件类型
 */
fun File.getFileType(skipBigFile: Boolean = false): String {
    if (this.isDirectory) {
        return FileType.FOLDER.typeName
    }
    if (skipBigFile) {
        if (this.length() > MAGIC_MAX_SIZE) {
            return getFileTypeUseSuffix();
        }
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
fun File.isTextFile(skipBigFile: Boolean = false): Boolean {
    if (this.isDirectory) {
        return false
    }
    if (skipBigFile) {
        if (this.length() > MAGIC_MAX_SIZE) {
            return true
        }
    }
    var mathch = Magic.getMagicMatch(this, true)
    return mathch != null && mathch.mimeType.startsWith("text")
}

/**
 * 获取文件属性
 */

fun File.getAttribute(skipBigFile: Boolean = false): FileAttribute {
    var readAttributes = Files.getFileAttributeView(this.toPath(), BasicFileAttributeView::class.java).readAttributes()
    return FileAttribute(
        this.toString(),
        this.getFileType(skipBigFile),
        this.getFileSize(),
        this.name,
        this.getFileSuffixValue(),
        readAttributes,
        readAttributes.creationTime().toMillis(),
        readAttributes.lastAccessTime().toMillis(),
        readAttributes.lastModifiedTime().toMillis(),
        Files.getOwner(this.toPath()).name,
        isTextFile(skipBigFile)
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
