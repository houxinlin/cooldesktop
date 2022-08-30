package com.hxl.desktop.web.app.web

import com.hxl.desktop.common.kotlin.extent.toFile
import com.hxl.desktop.common.kotlin.extent.toPath
import com.hxl.desktop.common.model.FileHandlerResult
import com.hxl.desktop.file.bean.FileAttribute
import com.hxl.desktop.file.bean.UploadInfo
import com.hxl.desktop.file.extent.getAttribute
import com.hxl.desktop.file.service.IFileService
import com.hxl.desktop.system.ano.LogRecord
import com.hxl.desktop.system.ano.UnifiedApiResult
import org.springframework.core.io.FileSystemResource
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.concurrent.Future
import javax.annotation.Resource
import kotlin.io.path.exists

/**
 * 文件操作相关接口定义
 */
@RequestMapping("/desktop/api/file")
@RestController
@UnifiedApiResult
class StaticResourceController {


    @Resource
    lateinit var fileSystemService: IFileService;


    /**
    * @description: 创建共享文件链接
    * @date: 2022/8/30 上午1:48
    */

    @PostMapping("share/link/create")
    fun createShareLink(@RequestParam("path")path:String):FileHandlerResult{
       return fileSystemService.createShareLink(path)
    }
    /**
     *tail 日志追踪
     */
    @LogRecord(logName = "停止日志追逐")
    @PostMapping("tail/stop")
    fun tailStop(@RequestParam("id") path: String): FileHandlerResult {
        return fileSystemService.tailStop(path)
    }

    /**
     *tail 日志追踪
     */
    @LogRecord(logName = "启动日志追逐")
    @PostMapping("tail/start")
    fun tailStart(@RequestParam("path") path: String): FileHandlerResult {
        return fileSystemService.tailStart(path)
    }

    /**
     *运行一个shell
     */
    @LogRecord(logName = "运行shell")
    @PostMapping("runShell")
    fun runShell(@RequestParam("path") path: String): Future<String> {
        return fileSystemService.runShell(path)
    }

    /**
     * 停止一个jar文件，如果存在此路径下多个进程，则终止失败
     */
    @LogRecord(logName = "停止jar")
    @PostMapping("stopJar")
    fun stopJar(@RequestParam("path") path: String): String {
        return fileSystemService.stopJar(path)
    }

    /**
     * 运行一个Jar文件
     */
    @LogRecord(logName = "运行jar")
    @PostMapping("runJar")
    fun runJarFile(
        @RequestParam("path") path: String,
        @RequestParam(value = "arg", required = false, defaultValue = "") arg: String,
        @RequestParam("type") type: Int,
    ): Boolean {
        return fileSystemService.runJarFile(path, arg, type)
    }

    /**
     *创建文件
     */
    @LogRecord(logName = "创建文件")
    @PostMapping("createFile")
    fun createFile(
        @RequestParam("parent") parent: String,
        @RequestParam("name") name: String,
        @RequestParam("type") type: String
    ): FileHandlerResult {
        return fileSystemService.createFile(parent, name, type)
    }

    /**
     * 文件重命名
     */
    @LogRecord(logName = "文件重命名")
    @PostMapping("fileRename")
    fun fileCopy(
        @RequestParam("source") source: String,
        @RequestParam("newName") newName: String
    ): FileHandlerResult {
        return fileSystemService.fileRename(source, newName)
    }

    /**
     * 文件复制
     */
    @LogRecord(logName = "文件复制")
    @PostMapping("fileCopy")
    fun fileCopy(@RequestParam("path") path: String): Boolean {
        return fileSystemService.fileCopy(path)
    }

    /**
     * 文件剪切
     */
    @LogRecord(logName = "文件剪切")
    @PostMapping("fileCut")
    fun fileCut(@RequestParam("path") path: String): Boolean {
        return fileSystemService.fileCut(path)
    }


    /**
     * 文件粘贴，粘贴的任务比较耗时，异步处理
     */
    @LogRecord(logName = "文件粘贴")
    @PostMapping("filePaste")
    fun filePaste(@RequestParam("path") target: String, @RequestParam("taskId") taskId: String): FileHandlerResult {
        fileSystemService.filePaste(target, taskId)
        return FileHandlerResult.TASK_SUBMIT_OK
    }

    /**
     * 文件上传
     */
    @LogRecord(logName = "文件上传")
    @PostMapping("chunkUpload")
    fun chunkUpload(uploadInfo: UploadInfo): FileHandlerResult {
        return fileSystemService.chunkUpload(uploadInfo)
    }

    /**
     * 合并块文件
     */
    @LogRecord(logName = "合并块文件")
    @PostMapping("chunkFileMerge")
    fun chunkFileMerge(
        @RequestParam(value = "name") name: String,
        @RequestParam(value = "targetName") targetName: String,
        @RequestParam(value = "inPath") inPath: String,
    ): FileHandlerResult {
        return fileSystemService.fileMerge(name, targetName, inPath)
    }

    /**
     * 文件删除
     */
    @LogRecord(logName = "文件删除")
    @GetMapping("/delete")
    fun delete(@RequestParam("path") root: String): FileHandlerResult {
        return fileSystemService.deleteFile(root)
    }

    /**
     * 文件list
     */

    @GetMapping("/list")
    fun list(@RequestParam("root") root: String): List<FileAttribute> {
        return fileSystemService.listDirector(root)
    }

    /**
     * 获取文件icon
     */
    @GetMapping("getFileIconByType")
    fun getFileIconByType(@RequestParam("type") type: String): ResponseEntity<org.springframework.core.io.Resource> {
        val header = HttpHeaders()
        header.add(HttpHeaders.CONTENT_TYPE, "image/png")
        val fileIcon = fileSystemService.getFileIconByType(type)
        return ResponseEntity.ok()
            .headers(header)
            .contentLength(fileIcon.contentLength())
            .body(fileIcon)
    }

    /**
     * 获取文件icon
     */
    @GetMapping("getFileIconByPath")
    fun getFileIconByPath(@RequestParam("path") path: String): ResponseEntity<org.springframework.core.io.Resource> {
        val header = HttpHeaders()
        header.add(HttpHeaders.CONTENT_TYPE, "image/png")
        val fileIcon = fileSystemService.getFileIconByPath(path)
        return ResponseEntity.ok()
            .headers(header)
            .contentLength(fileIcon.contentLength())
            .body(fileIcon)
    }

    /**
     * 文件预览图
     */
    @GetMapping("getImageThumbnail")
    fun getImageThumbnail(@RequestParam("path") path: String): ResponseEntity<org.springframework.core.io.Resource> {
        val header = HttpHeaders()
        header.add(HttpHeaders.CONTENT_TYPE, "image/png")
        val fileIcon = fileSystemService.getImageThumbnail(path)
        return ResponseEntity.ok()
            .headers(header)
            .contentLength(fileIcon.contentLength())
            .body(fileIcon)
    }

    /**
     * 文件压缩
     */
    @LogRecord(logName = "文件压缩")
    @PostMapping("fileCompress")
    fun fileCompress(
        @RequestParam("path") path: String,
        @RequestParam("targetName") targetName: String,
        @RequestParam("compressType") compressType: String,
        @RequestParam("taskId") taskId: String
    ): Future<FileHandlerResult> {
        return fileSystemService.fileCompress(path, targetName, compressType, taskId)
    }

    /**
     * 文件解压
     */
    @LogRecord(logName = "文件解压")
    @PostMapping("fileDecompression")
    fun fileCompress(
        @RequestParam("path") path: String,
        @RequestParam("taskId") taskId: String
    ): Future<FileHandlerResult> {
        return fileSystemService.fileDecompression(path, taskId)
    }

    /**
     * 文件属性
     */
    @PostMapping("getFileAttribute")
    fun getFileAttribute(@RequestParam("path") path: String): FileHandlerResult {
        if (path.toPath().exists()) {
            return FileHandlerResult.createOK(path.toFile().getAttribute())
        }
        return FileHandlerResult.NOT_EXIST
    }

    /**
     * 获取文本内容
     */
    @GetMapping("getTextFileContent")
    fun getTextFileContent(@RequestParam("path") path: String): FileHandlerResult {
        if (path.toPath().exists()) {
            return fileSystemService.getTextFileContent(path)
        }
        return FileHandlerResult.NOT_EXIST
    }

    /**
     * 设置文本内容
     */
    @LogRecord(logName = "设置文本内容")
    @PostMapping("setTextFileContent")
    fun setTextFileContent(
        @RequestParam("path") path: String,
        @RequestParam("content") content: String
    ): FileHandlerResult {
        return fileSystemService.setTextFileContent(path, content)
    }

    /**
     * 下载
     */
    @LogRecord(logName = "下载文件")
    @GetMapping("download")
    fun download(@RequestParam("path") path: String): ResponseEntity<FileSystemResource> {
        return fileSystemService.download(path)
    }


}