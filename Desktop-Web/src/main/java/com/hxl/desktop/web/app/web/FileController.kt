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

    /**
     *file rename
     */
    @PostMapping("fileRename")
    fun fileCopy(@RequestParam("source") source: String,
                 @RequestParam("newName") newName: String): Any {
        return iFileService.fileRename(source, newName).asHttpResponseBody()
    }

    /**
     * file copy
     */
    @PostMapping("fileCopy")
    fun fileCopy(@RequestParam("path") path: String): Any {
        return iFileService.fileCopy(path).asHttpResponseBody()
    }

    /**
     * file cut
     */
    @PostMapping("fileCut")
    fun fileCut(@RequestParam("path") path: String): Any {
        return iFileService.fileCut(path).asHttpResponseBody()
    }


    /**
     * file paste
     */
    @PostMapping("filePaste")
    fun filePaste(@RequestParam("path") target: String): Any {
        return iFileService.filePaste(target).asHttpResponseBody()
    }

    /**
     * chunk file upload
     */

    @PostMapping("chunkUpload")
    fun chunkUpload(uploadInfo: UploadInfo): Any {
        return iFileService.checkUploadFile(uploadInfo).asHttpResponseBody()
    }

    /**
     * chunk file mearge
     */
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

    @PostMapping("fileCompress")
    fun fileCompress(@RequestParam("path") path: String,
                     @RequestParam("targetName") targetName: String,
                     @RequestParam("compressType") compressType: String): Any {
        return iFileService.fileCompress(path, targetName, compressType)
    }

}