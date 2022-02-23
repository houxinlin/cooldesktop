package common.extent

import common.bean.HttpResponseBody
import common.bean.HttpResponseStatus
import common.result.BaseHandlerResult


/**
 * @author:   HouXinLin
 * @email:    2606710413@qq.com
 * @date:     20212021/12/18
 * @describe: any extent
 * @version:  v1.0
 */
class AnyExtent {
}

fun Any.asHttpResponseBody(): HttpResponseBody {
    if (this is HttpResponseBody) {
        return this;
    }
    if (this is BaseHandlerResult) {
        return HttpResponseBody(this.code, this.data, this.msg);
    }
    return HttpResponseBody(0, this, "OK");
}

fun Any.asHttpResponseBody(httpStatus: HttpResponseStatus): HttpResponseBody {
    return HttpResponseBody(httpStatus.status, this, httpStatus.msg);
}
