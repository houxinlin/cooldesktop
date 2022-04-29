package com.hxl.desktop.web.app.web

import com.hxl.desktop.common.extent.asHttpResponseBody
import com.hxl.desktop.common.extent.toFile
import com.hxl.desktop.common.extent.toPath
import com.hxl.desktop.common.result.FileHandlerResult
import com.hxl.desktop.file.bean.UploadInfo
import com.hxl.desktop.file.extent.getAttribute
import com.hxl.desktop.file.service.IFileService
import org.springframework.core.io.FileSystemResource
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.annotation.Resource
import kotlin.io.path.exists

/**
 * @author:   HouXinLin
 * @email:    2606710413@qq.com
 * @date:     20212021/12/18
 * @describe: 静态资源控制器
 * @version:  v1.0
 */
@RequestMapping("/desktop/api/file")
@RestController()
class StaticResourceController {


    @Resource
    lateinit var fileSystemService: IFileService;

    /**
     * 停止一个jar文件，如果存在此路径下多个进程，则终止失败
     */
    @PostMapping("stopJar")
    fun stopJar(@RequestParam("path") path: String): Any {
        return fileSystemService.stopJar(path).asHttpResponseBody()
    }

    /**
     * 运行一个Jar文件
     */
    @PostMapping("runJar")
    fun runJarFile(
        @RequestParam("path") path: String,
        @RequestParam(value = "arg", required = false, defaultValue = "") arg: String
    ): Any {
        return fileSystemService.runJarFile(path, arg).asHttpResponseBody()
    }

    /**
     *创建文件
     */
    @PostMapping("createFile")
    fun createFile(
        @RequestParam("parent") parent: String,
        @RequestParam("name") name: String,
        @RequestParam("type") type: String
    ): Any {
        return fileSystemService.createFile(parent, name, type).asHttpResponseBody()
    }

    /**
     * 文件重命名
     */
    @PostMapping("fileRename")
    fun fileCopy(
        @RequestParam("source") source: String,
        @RequestParam("newName") newName: String
    ): Any {
        return fileSystemService.fileRename(source, newName).asHttpResponseBody()
    }

    /**
     * 文件复制
     */
    @PostMapping("fileCopy")
    fun fileCopy(@RequestParam("path") path: String): Any {
        return fileSystemService.fileCopy(path).asHttpResponseBody()
    }

    /**
     * 文件剪切
     */
    @PostMapping("fileCut")
    fun fileCut(@RequestParam("path") path: String): Any {
        return fileSystemService.fileCut(path).asHttpResponseBody()
    }


    /**
     * 文件粘贴
     */
    @PostMapping("filePaste")
    fun filePaste(@RequestParam("path") target: String, @RequestParam("taskId") taskId: String): Any {
        fileSystemService.filePaste(target, taskId)
        return FileHandlerResult.TASK_SUBMIT_OK
    }

    /**
     * 文件上传
     */

    @PostMapping("chunkUpload")
    fun chunkUpload(uploadInfo: UploadInfo): Any {
        return fileSystemService.chunkUpload(uploadInfo).asHttpResponseBody()
    }

    /**
     * 合并块文件
     */
    @PostMapping("chunkFileMerge")
    fun chunkFileMerge(
        @RequestParam(value = "name") name: String,
        @RequestParam(value = "targetName") targetName: String,
        @RequestParam(value = "inPath") inPath: String,
    ): Any {
        return fileSystemService.fileMerge(name, targetName, inPath).asHttpResponseBody()
    }

    /**
     * 文件删除
     */
    @GetMapping("/delete")
    fun delete(@RequestParam("path") root: String): Any {
        return fileSystemService.deleteFile(root).asHttpResponseBody()
    }

    /**
     * 文件list
     */
    @GetMapping("/list")
    fun list(@RequestParam("root") root: String): Any {
        return fileSystemService.listDirector(root).asHttpResponseBody()
    }

    /**
     * 获取文件icon
     */
    @GetMapping("getFileIconByType")
    fun getFileIconByType(@RequestParam("type") path: String): ResponseEntity<org.springframework.core.io.Resource> {
        var header = HttpHeaders();
        header.add(HttpHeaders.CONTENT_TYPE, "image/png")
        var fileIcon = fileSystemService.getFileIconByType(path)
        return ResponseEntity.ok()
            .headers(header)
            .contentLength(fileIcon.contentLength())
            .body(fileIcon);
    }

    /**
     * 文件预览图
     */
    @GetMapping("getImageThumbnail")
    fun getImageThumbnail(@RequestParam("path") path: String): ResponseEntity<org.springframework.core.io.Resource> {
        var header = HttpHeaders();
        header.add(HttpHeaders.CONTENT_TYPE, "image/png")
        var fileIcon = fileSystemService.getImageThumbnail(path)
        return ResponseEntity.ok()
            .headers(header)
            .contentLength(fileIcon.contentLength())
            .body(fileIcon);
    }

    /**
     * 文件压缩
     */
    @PostMapping("fileCompress")
    fun fileCompress(
        @RequestParam("path") path: String,
        @RequestParam("targetName") targetName: String,
        @RequestParam("compressType") compressType: String,
        @RequestParam("taskId") taskId: String
    ): Any {
        return fileSystemService.fileCompress(path, targetName, compressType, taskId)
    }

    /**
     * 文件解压
     */
    @PostMapping("fileDecompression")
    fun fileCompress(
        @RequestParam("path") path: String,
        @RequestParam("taskId") taskId: String
    ): Any {
        return fileSystemService.fileDecompression(path, taskId)
    }

    /**
     * 文件属性
     */
    @PostMapping("getFileAttribute")
    fun getFileAttribute(@RequestParam("path") path: String): Any {
        if (path.toPath().exists()) {
            return path.toFile().getAttribute().asHttpResponseBody()
        }
        return FileHandlerResult.NOT_EXIST.asHttpResponseBody()
    }

    /**
     * 获取文本内容
     */
    @GetMapping("getTextFileContent")
    fun getTextFileContent(@RequestParam("path") path: String): Any {
        if (path.toPath().exists()) {
            return fileSystemService.getTextFileContent(path).asHttpResponseBody()
        }
        return FileHandlerResult.NOT_EXIST.asHttpResponseBody()
    }

    /**
     * 设置文本内容
     */
    @PostMapping("setTextFileContent")
    fun setTextFileContent(@RequestParam("path") path: String, @RequestParam("content") content: String): Any {
        return fileSystemService.setTextFileContent(path, content).asHttpResponseBody()
    }

    /**
     * 下载
     */
    @GetMapping("download")
    fun download(@RequestParam("path") path: String): ResponseEntity<FileSystemResource> {
        return fileSystemService.download(path)
    }

}