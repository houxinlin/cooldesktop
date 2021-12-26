package com.hxl.desktop.web.app.web

import com.hxl.desktop.common.bean.UploadInfo
import com.hxl.desktop.file.service.IFileService
import com.hxl.desktop.common.extent.asHttpResponseBody
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
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

    @PostMapping("chunkUpload")
    fun chunkUpload( uploadInfo: UploadInfo): Any {
        return iFileService.checkUploadFile(uploadInfo)
    }

    @PostMapping("chunkFileMerge")
    fun chunkFileMerge(
            @RequestParam(value = "name") name: String,
            @RequestParam(value = "targetName") targetName: String,
            @RequestParam(value = "inPath") inPath: String,
    ): Any {
        return iFileService.fileMerge(name, targetName, inPath).asHttpResponseBody()
        return "OK";
    }

    /**
     * delete files
     */
    @GetMapping("/delete")
    fun delete(@RequestParam("path") root: String): Any {
        return iFileService.deleteFile(root).asHttpResponseBody()
    }

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
    @GetMapping("getFileIconByType")
    fun getFileIconByType(@RequestParam("type") path: String): ResponseEntity<org.springframework.core.io.Resource> {
        var header = HttpHeaders();
        header.add(HttpHeaders.CONTENT_TYPE, "image/png")
        var fileIcon = iFileService.getFileIconByType(path)
        return ResponseEntity.ok()
                .headers(header)
                .contentLength(fileIcon.contentLength())
                .body(fileIcon);
    }

    /**
     * get file preview image
     */
    @GetMapping("getImageThumbnail")
    fun getImageThumbnail(@RequestParam("path") path: String): ResponseEntity<org.springframework.core.io.Resource> {
        var header = HttpHeaders();
        header.add(HttpHeaders.CONTENT_TYPE, "image/png")
        var fileIcon = iFileService.getImageThumbnail(path)
        return ResponseEntity.ok()
                .headers(header)
                .contentLength(fileIcon.contentLength())
                .body(fileIcon);
    }

}