package com.hxl.desktop.file.extent

import com.hxl.desktop.common.bean.FileAttribute
import com.hxl.desktop.common.extent.toPath
import com.hxl.desktop.file.utils.Directory
import java.io.IOException
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributeView
import java.nio.file.attribute.BasicFileAttributes
import java.util.stream.Collectors
import kotlin.io.path.exists
import kotlin.io.path.isDirectory

class PathExtent {
}

fun Path.walkFileTree(): MutableList<String> {
    var mutableListOf = mutableListOf<String>()
    Files.walkFileTree(this, object : SimpleFileVisitor<Path>() {
        override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
            mutableListOf.add(file.toString())
            return FileVisitResult.CONTINUE
        }

        override fun postVisitDirectory(dir: Path, exc: IOException?): FileVisitResult {
            if (dir.toString() != this@walkFileTree.toString()) {
                mutableListOf.add(dir.toString())
            }
            return FileVisitResult.CONTINUE
        }
    })
    return mutableListOf
}

fun Path.listRootDirector(): List<String> {
    if (!this.exists()) return emptyList()
    if (!this.isDirectory()) return emptyList()
    return Files.list(this).map { it.toString() }.collect(Collectors.toList())
}
