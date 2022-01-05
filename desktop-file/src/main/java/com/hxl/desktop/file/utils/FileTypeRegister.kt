package com.hxl.desktop.file.utils

/**
 * @author:   HouXinLin
 * @email:    2606710413@qq.com
 * @date:     20212021/12/18
 * @describe:
 * @version:  v1.0
 */
class FileTypeRegister {

    companion object {
        const val PREFIX = "/static/icon/ic-";
        val IMAGE = arrayOf("jpg", "jpeg", "png", "bmp");
        val ZIP= arrayOf("zip","rar","gz","7z")
        fun getFullPath(key: String): String {
            return "${PREFIX}${key}.png";
        }
    }
}