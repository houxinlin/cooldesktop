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
    * @description: 获取空间使用情况
    * @date: 2022/9/3 下午7:23
    */
    @GetMapping("/space/status")
    fun getSpaceUse(@RequestParam(value = "root", required = false, defaultValue = "/")root:String):FileHandlerResult{
            return fileSystemService.getSpaceUse(root)
    }
    /**
    * @description: 创建共享文件链接
     * @param path 文件路径
     * @param day  多少天过期
    * @date: 2022/8/30 上午1:48
    */
    @LogRecord(logName = "创建共享链接")
    @PostMapping("share/link/create")
    fun createShareLink(@RequestParam("path")path:String,@RequestParam("day")day:String):FileHandlerResult{
       return fileSystemService.createShareLink(path,day)
    }
    
    /**
    * @description: 获取共享的share link
    * @date: 2022/9/1 上午2:19
    */
    
    @GetMapping("share/link/list")
    fun listShareLink():FileHandlerResult{
        return fileSystemService.listShareLink()
    }

    /**
    * @description: 删除共享链接
     * @param id 共享id
    * @date: 2022/9/1 下午7:15
    */

    @PostMapping("share/link/delete")
    fun deleteShareLink(@RequestParam("id") id:String):FileHandlerResult{
        return fileSystemService.deleteShareLink(id)
    }

   /**
   * @description: tail 日志追踪
   * @date: 2022/9/1 下午7:16
    * @param
   */

    @LogRecord(logName = "停止日志追逐")
    @PostMapping("tail/stop")
    fun tailStop(@RequestParam("id") id: String): FileHandlerResult {
        return fileSystemService.tailStop(id)
    }


    /**
    * @description: tail 日志追踪
    * @date: 2022/9/1 下午7:17
     * @param path 追踪的文件路径
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
    @PostMapping("shell/run")
    fun runShell(@RequestParam("path") path: String): Future<String> {
        return fileSystemService.runShell(path)
    }

    /**
     * 停止一个jar文件，如果存在此路径下多个进程，则终止失败
     */
    @LogRecord(logName = "停止jar")
    @PostMapping("jar/stop")
    fun stopJar(@RequestParam("path") path: String): String {
        return fileSystemService.stopJar(path)
    }

    /**
     * 运行一个Jar文件
     */
    @LogRecord(logName = "运行jar")
    @PostMapping("jar/run")
    fun runJarFile(
        @RequestParam("path") path: String,
        @RequestParam(value = "jvmArg", required = false, defaultValue = "") jvmArg: String,
        @RequestParam(value = "applicationArg", required = false, defaultValue = "") applicationArg: String,
        @RequestParam(value = "logPath", required = false, defaultValue = "") logPath: String,
        @RequestParam("type") type: Int,
    ): Boolean {
        return fileSystemService.runJarFile(path, jvmArg,applicationArg,logPath, type)
    }

    /**
     *创建文件
     */
    @LogRecord(logName = "创建文件")
    @PostMapping("create")
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
    @PostMapping("rename")
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
    @PostMapping("copy")
    fun fileCopy(@RequestParam("path") path: String): Boolean {
        return fileSystemService.fileCopy(path)
    }

    /**
     * 文件剪切
     */
    @LogRecord(logName = "文件剪切")
    @PostMapping("cut")
    fun fileCut(@RequestParam("path") path: String): Boolean {
        return fileSystemService.fileCut(path)
    }


    /**
     * 文件粘贴，粘贴的任务比较耗时，异步处理
     */
    @LogRecord(logName = "文件粘贴")
    @PostMapping("paste")
    fun filePaste(@RequestParam("path") target: String, @RequestParam("taskId") taskId: String): FileHandlerResult {
        fileSystemService.filePaste(target, taskId)
        return FileHandlerResult.TASK_SUBMIT_OK
    }

    /**
     * 文件上传
     */
    @LogRecord(logName = "文件上传")
    @PostMapping("chunk/upload")
    fun chunkUpload(uploadInfo: UploadInfo): FileHandlerResult {
        return fileSystemService.chunkUpload(uploadInfo)
    }

    /**
     * 合并块文件
     */
    @LogRecord(logName = "合并块文件")
    @PostMapping("chunk/file/merge")
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
    @GetMapping("icon/by/type/get")
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
    @GetMapping("icon/by/path/get")
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
    @GetMapping("image/thumbnail/get")
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
    @PostMapping("compress")
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
    @PostMapping("decompression")
    fun fileCompress(
        @RequestParam("path") path: String,
        @RequestParam("taskId") taskId: String
    ): Future<FileHandlerResult> {
        return fileSystemService.fileDecompression(path, taskId)
    }

    /**
     * 文件属性
     */
    @PostMapping("attribute/get")
    fun getFileAttribute(@RequestParam("path") path: String): FileHandlerResult {
        if (path.toPath().exists()) {
            return FileHandlerResult.createOK(path.toFile().getAttribute())
        }
        return FileHandlerResult.NOT_EXIST
    }

    /**
     * 获取文本内容
     */
    @GetMapping("text/content/get")
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
    @PostMapping("text/content/set")
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