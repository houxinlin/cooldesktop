package common.result

import java.util.*

/**
 * @author:   HouXinLin
 * @email:    2606710413@qq.com
 * @date:     20212021/12/20
 * @describe:
 * @version:  v1.0
 */

class FileHandlerResult(code: Int, data: Any, msg: String) : BaseHandlerResult(code, data, msg) {
    companion object {
        val OK = FileHandlerResult(0, "", "OK");
        val EXIST = FileHandlerResult(-1, "", "文件存在");
        val NOT_EXIST = FileHandlerResult(-1, "", "文件不存在");
        val MERGE_ERROR = FileHandlerResult(-2, "", "文件存在");
        val NO_PERMISSION = FileHandlerResult(-3, "", "无权限对当前文件执行此操作");
        val NONE = FileHandlerResult(-4, "", "未知错误");
        val TARGET_EXIST = FileHandlerResult(-5, "", "目标已存在")
        val TARGET_NOT_EXIST = FileHandlerResult(-6, "", "目标不存在")
        val NULL = FileHandlerResult(-7, "", "null object")
        val NO_SELECT_FILE = FileHandlerResult(-8, "", "没有选择任何文件")
        val CANNOT_COPY = FileHandlerResult(-9, "", "无法复制")
        val CREATE_FILE_FAIL = FileHandlerResult(-10, "", "无法创建文件")
        val TASK_SUBMIT_OK = FileHandlerResult(0, "", "任务提交成功")
        val UPLOAD_FAIL = FileHandlerResult(0, "", "上传失败")
        val CANNOT_MERGE = FileHandlerResult(-11, "", "无法合并")
        fun create(code: Int, data: Any, msg: String): FileHandlerResult {
            return FileHandlerResult(code, data, msg);
        }

        fun withAsyncId(): FileHandlerResult {
            return FileHandlerResult(0, UUID.randomUUID().toString(), "任务提交成功");
        }
    }
}
