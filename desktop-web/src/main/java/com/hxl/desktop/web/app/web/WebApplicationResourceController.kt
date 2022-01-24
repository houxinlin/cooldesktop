package com.hxl.desktop.web.app.web

import com.hxl.desktop.loader.application.ApplicationRegister
import com.hxl.desktop.loader.application.webmini.WebMiniApplication
import com.hxl.desktop.web.util.MediaUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.HandlerMapping
import java.nio.file.Paths
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@RestController
@RequestMapping("/desktop/webapplication/")
class WebApplicationResourceController {
    @Autowired
    lateinit var applicationRegister: ApplicationRegister

    @GetMapping("{applicationId}/**")
    fun getResource(
        @PathVariable("applicationId") applicationId: String,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ResponseEntity<Resource> {
        var webMinApplication = applicationRegister.getWebMinApplication(applicationId)
        if (webMinApplication == null) {
            return ResponseEntity.notFound().build()
        }
        val restOfTheUrl = request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE) as String
        val res = restOfTheUrl.removePrefix("/desktop/webapplication/${applicationId}")
        var fileLastValue = Paths.get(restOfTheUrl).last().toString()
        (webMinApplication as WebMiniApplication)?.let {
            var loadResource = it.loadResource(res)
            if (loadResource==null){
                return ResponseEntity.notFound().build()
            }
            val resource = ByteArrayResource(loadResource)
            val header = HttpHeaders()
            return ResponseEntity.ok()
                .headers(header)
                .contentLength(resource.contentLength())
                .contentType(MediaUtils.getFileMimeType(fileLastValue))
                .body(resource);
        }

    }

}