package common.bean

/**
 * @author:   HouXinLin
 * @email:    2606710413@qq.com
 * @date:     20212021/12/18
 * @describe: http response body
 * @version:  v1.0
 */
data class HttpResponseBody(var status: Int, var data: Any, var msg: String) {

}

fun failResponse(msg: String): HttpResponseBody {
    return HttpResponseBody(-1, "", msg)
}
