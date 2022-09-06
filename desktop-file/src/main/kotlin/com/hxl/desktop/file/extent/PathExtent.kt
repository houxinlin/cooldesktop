package com.hxl.desktop.file.extent

import java.io.File
import java.io.IOException
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import java.util.stream.Collectors
import kotlin.io.path.exists
import kotlin.io.path.isDirectory

class PathExtent {
}

fun Path.walkFileTree(suffix: String = "", onlyFile: Boolean = false): MutableList<Path> {
    val mutableListOf = mutableListOf<Path>()
    Files.walkFileTree(this, object : SimpleFileVisitor<Path>() {
        override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
            if ("" == suffix) {
                mutableListOf.add(file)
            } else {
                if (file.fileName.toString().endsWith(suffix)) {
                    mutableListOf.add(file)
                }
            }
            return FileVisitResult.CONTINUE
        }

        override fun postVisitDirectory(dir: Path, exc: IOException?): FileVisitResult {
            if (!onlyFile) {
                if (dir.toString() != this@walkFileTree.toString()) {
                    mutableListOf.add(dir)
                }
            }
            return FileVisitResult.CONTINUE
        }
    })
    return mutableListOf
}

//列举当前目录
fun Path.listRootDirector(): List<File> {
    if (!this.toFile().canRead()) return emptyList()
    if (!this.exists()) return emptyList()
    if (!this.isDirectory()) return emptyList()
    return Files.list(this).map { it.toFile() }.filter {
        !Files.isSymbolicLink(it.toPath())
    }.collect(Collectors.toList())
}


