package com.hxl.desktop.web.app.web

import com.hxl.desktop.common.kotlin.extent.toHttpResponse
import com.hxl.desktop.loader.application.ApplicationManager
import org.apache.tika.Tika
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.HandlerMapping
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * web应用资源
 */
@RestController
@RequestMapping("/desktop/webapplication/")
class WebApplicationResourceController {
    companion object {
        const val WEB_MINE_REQUEST_PREFIX = "/desktop/webapplication/"
    }

    @Autowired
    lateinit var applicationManager: ApplicationManager

    /**
     * @description: 根据应用程序id获取对应的资源
     * @date: 2022/8/21 上午1:06
     */
    @GetMapping("{applicationId}/**")
    fun getResource(@PathVariable("applicationId") applicationId: String,
                    request: HttpServletRequest,
                    response: HttpServletResponse
    ): ResponseEntity<Resource> {
        val application = applicationManager.getApplicationById(applicationId) ?: return ResponseEntity.notFound().build()
        val fullUrl = request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE) as String
        val pathOfApplication = fullUrl.removePrefix("${WEB_MINE_REQUEST_PREFIX}${applicationId}")
        //加载资源
        val requestResource = application.loadResource(pathOfApplication)
        requestResource?.run {
            return requestResource.toHttpResponse(MediaType.parseMediaType(Tika().detect(pathOfApplication)))
        }
        return ResponseEntity.notFound().build()
    }

}