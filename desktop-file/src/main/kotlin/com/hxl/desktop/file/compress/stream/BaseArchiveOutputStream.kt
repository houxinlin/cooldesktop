package com.hxl.desktop.file.compress.stream

interface BaseArchiveOutputStream<T, E> {
    fun putArchiveEntry(name: String, file: String)
    fun createEntry(name: String, file: String): E
    fun close()
}