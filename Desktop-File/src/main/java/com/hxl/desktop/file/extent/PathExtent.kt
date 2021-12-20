package com.hxl.desktop.file.extent

import com.hxl.desktop.common.bean.FileAttribute
import com.hxl.desktop.file.utils.Directory
import java.nio.file.Path
import kotlin.io.path.notExists

/**
 * @author:   HouXinLin
 * @email:    2606710413@qq.com
 * @date:     20212021/12/18
 * @describe:
 * @version:  v1.0
 */
class PathExtent {
}

fun Path.toFileAttribute(): FileAttribute? {
    if (this.notExists()) {
        return null;
    }
    return Directory.getFileAttribute(this);
}