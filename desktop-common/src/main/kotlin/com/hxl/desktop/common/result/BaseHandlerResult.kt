package common.result

/**
 * @author:   HouXinLin
 * @email:    2606710413@qq.com
 * @date:     20212021/12/20
 * @describe:
 * @version:  v1.0
 */
open class BaseHandlerResult(val code: Int, val data: Any, val msg: String) {
    override fun toString(): String {
        return "BaseHandlerResult(code=$code, data=$data, msg='$msg')"
    }
}