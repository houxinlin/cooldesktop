package com.hxl.desktop.common.extent

import java.io.IOException
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.util.stream.Collectors

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

fun Path.listRootDirector(): MutableList<String> {
    var mutableListOf = mutableListOf<String>()
    return Files.list(this).map { it.toString() }.collect(Collectors.toList())
}