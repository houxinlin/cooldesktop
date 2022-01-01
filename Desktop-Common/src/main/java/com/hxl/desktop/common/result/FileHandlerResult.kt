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
        val OK = FileHandlerResult(0, "", "OK");
        val EXIST = FileHandlerResult(-1, "", "文件存在");
        val NOW_EXIST = FileHandlerResult(-1, "", "文件bu存在");
        val MERGE_ERROR = FileHandlerResult(-2, "", "文件存在");
        val NO_PERMISSION = FileHandlerResult(-3, "", "无权限对次文件执行任何操作");
        val NONE = FileHandlerResult(-4, "", "未知错误");
        val TARGET_EXIST = FileHandlerResult(-5, "", "目标已存在")
        val TARGET_NOT_EXIST = FileHandlerResult(-6, "", "目标不存在")
        val NULL = FileHandlerResult(-7, "", "null object")
        val NO_SELECT_FILE = FileHandlerResult(-8, "", "没有选择任何文件")
        val CANNOT_COPY= FileHandlerResult(-8, "", "无法复制")

    }
}
