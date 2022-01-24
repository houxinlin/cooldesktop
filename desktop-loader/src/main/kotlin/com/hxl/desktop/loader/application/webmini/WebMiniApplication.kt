package com.hxl.desktop.loader.application.webmini

import com.hxl.desktop.loader.application.Application
import com.hxl.fm.pk.FilePackage
import java.io.BufferedInputStream
import java.io.FileInputStream
import java.nio.ByteBuffer

/**
 * 这个文件前n个字节是程序信息，后面字节是静态资源文件
 */
class WebMiniApplication : Application() {

    /**
     * 路径
     */
    var applicationPath: String = "";

    /**
     * 静态资源偏移
     */
    var staticResOffset: Long = 0;


    fun loadResource(path: String): ByteArray? {

        if (!inCache(path)) {
            var bufferedInputStream = BufferedInputStream(
                FileInputStream(applicationPath)
            )
            bufferedInputStream.skip(staticResOffset)
            var fileTable = FilePackage.decode(bufferedInputStream.readBytes())
            if (fileTable?.get(path) == null) {
                return null;
            }
            addCacheResource(path, fileTable.get(path)!!)
        }
        return getCacheResource(path)
    }
}