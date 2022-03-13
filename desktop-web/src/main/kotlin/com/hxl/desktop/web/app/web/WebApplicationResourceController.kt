package com.hxl.desktop.web.app.web

import com.hxl.desktop.common.extent.toHttpResponse
import com.hxl.desktop.loader.application.ApplicationRegister
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
    lateinit var applicationRegister: ApplicationRegister

    @GetMapping("{applicationId}/**")
    fun getResource(
        @PathVariable("applicationId") applicationId: String,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ResponseEntity<Resource> {
        var application = applicationRegister.getApplicationById(applicationId)

        val restOfTheUrl = request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE) as String
        val pathOfApplication = restOfTheUrl.removePrefix("${WEB_MINE_REQUEST_PREFIX}${applicationId}")
        //加载资源
        var requestResource = application?.loadResource(pathOfApplication)

        requestResource?.run {
            return requestResource.toHttpResponse(MediaType.parseMediaType(Tika().detect(pathOfApplication)))
        }
        return ResponseEntity.notFound().build()
    }

}