package com.hxl.desktop.loader.application.easyapp

import org.springframework.core.io.UrlResource
import java.nio.ByteBuffer
import java.util.jar.JarFile

class JarFileClassExtract {
    fun extract(jarFile: JarFile, easyApplicationClassCallback: EasyApplicationClassCallback) {
        jarFile.stream().forEach {
            //如果是class文件
            if (it.name.endsWith(EasyApplicationLoader.CLASS_NAME_SUFFIX)) {
                var path = EasyApplicationLoader.JAR_FILE_PREFIX + jarFile.name + "!/" + it.name;
                var classUrlResource = UrlResource(path)
                var byteArray = ByteArray(4)
                //读取前4个字节,用来二次确认
                classUrlResource.inputStream.read(byteArray)
                //如果是class类型
                if (isClass(byteArray)) {
                    var jarEntry = it
                    easyApplicationClassCallback.call(classUrlResource, jarEntry)
//                    registerIfComponentClass(classUrlResource, it, jarFile, componentClassNames)
                }
            }
        }
    }

    fun isClass(byteArray: ByteArray): Boolean {
        var wrap = ByteBuffer.wrap(byteArray)
        return wrap.limit() == 4 && (wrap.getInt(0) == -889275714)
    }

}