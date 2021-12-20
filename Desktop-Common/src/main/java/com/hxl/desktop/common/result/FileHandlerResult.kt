package com.hxl.desktop.common.result

/**
 * @author:   HouXinLin
 * @email:    2606710413@qq.com
 * @date:     20212021/12/20
 * @describe:
 * @version:  v1.0
 */

class FileHandlerResult(code: Int, data: String, msg: String) :
        BaseHandlerResult(code, data, msg) {
    companion object {
        val EXIST = FileHandlerResult(-1, "", "文件存在");
        val MERGE_ERROR = FileHandlerResult(-2, "", "文件存在");
        val OK = FileHandlerResult(0, "", "OK");
    }
}