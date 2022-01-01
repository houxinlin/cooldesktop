package com.hxl.desktop.file.extent

import com.hxl.desktop.common.bean.FileAttribute
import com.hxl.desktop.common.extent.toFile
import com.hxl.desktop.common.extent.toPath
import com.hxl.desktop.file.emun.FileType
import com.hxl.desktop.file.utils.FileTypeRegister
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributeView

class FileExtent {
}

fun File.getFileType(): String {
    if (this.isDirectory) {
        return FileType.FOLDER.typeName
    }
    var suffix = this.name.substring(this.name.lastIndexOf(".") + 1)
    if (suffix.isEmpty()) {
        return FileType.FILE.typeName
    }
    if (FileTypeRegister.IMAGE.contains(suffix.lowercase())) {
        return FileType.IMAGE.typeName
    }
    return suffix;
}

fun File.getRawFileType(): String {
    var suffix = this.name.substring(this.name.lastIndexOf(".") + 1)
    if (suffix.isNotEmpty()) {
        return suffix;
    }
    return "";
}

fun File.getFileSize(): Long {
    if (this.isDirectory) {
        return -1;
    }
    return Files.size(this.toPath())
}

fun File.getAttribute(): FileAttribute {
    var readAttributes = Files.getFileAttributeView(this.toPath(), BasicFileAttributeView::class.java).readAttributes()
    return FileAttribute(
        this.toString(),
        this.getFileType(),
        this.getFileSize(),
        this.name,
        this.getRawFileType(),
        readAttributes,
        readAttributes.creationTime().toMillis(),
        readAttributes.lastAccessTime().toMillis(),
        readAttributes.lastModifiedTime().toMillis(),
        Files.getOwner(this.toPath()).name
    )
}