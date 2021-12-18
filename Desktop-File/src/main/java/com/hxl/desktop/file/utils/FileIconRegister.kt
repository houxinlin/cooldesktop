package com.hxl.desktop.file.utils

/**
 * @author:   HouXinLin
 * @email:    2606710413@qq.com
 * @date:     20212021/12/18
 * @describe:
 * @version:  v1.0
 */
class FileIconRegister {

    companion object {
        val IMAGE = arrayOf("jpg","jpeg","png","webp","bmp");
        const val PREFIX = "/static/icon/ic-";
        fun getFullPath(key: String): String {
            return "${PREFIX}${key}.png";
        }
    }
}