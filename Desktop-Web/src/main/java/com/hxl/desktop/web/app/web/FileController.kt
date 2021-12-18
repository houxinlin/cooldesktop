package com.hxl.desktop.web.app.web

import com.hxl.desktop.file.service.IFileService
import com.hxl.desktop.web.extent.asHttpResponseBody
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.annotation.Resource

/**
 * @author:   HouXinLin
 * @email:    2606710413@qq.com
 * @date:     20212021/12/18
 * @describe: file controller
 * @version:  v1.0
 */
@RequestMapping("/desktop/api/file")
@RestController()
class FileController {
    @Resource
    lateinit var iFileService: IFileService;

    /**
     * list files
     */
    @GetMapping("/list")
    fun list(@RequestParam("root") root: String): Any {
        return iFileService.listDirector(root).asHttpResponseBody()
    }

    /**
     * get file preview image
     */
    @GetMapping("getFileIcon")
    fun getFileIcon(@RequestParam("path") path: String): ResponseEntity<org.springframework.core.io.Resource> {
        var header = HttpHeaders();
        header.add(HttpHeaders.CONTENT_TYPE,"image/png")
        var fileIcon = iFileService.getFileIcon(path)
        return ResponseEntity.ok()
                .headers(header)
                .contentLength(fileIcon.contentLength())
                .body(fileIcon);
    }

}