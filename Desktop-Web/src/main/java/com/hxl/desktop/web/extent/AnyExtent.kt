package com.hxl.desktop.web.extent

import com.hxl.desktop.web.bean.HttpResponseBody
import com.hxl.desktop.web.response.HttpResponseStatus

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
    return HttpResponseBody(0, this, "OK");
}

fun Any.asHttpResponseBody(httpStatus: HttpResponseStatus): HttpResponseBody {
    return HttpResponseBody(httpStatus.status, this, httpStatus.msg);
}
