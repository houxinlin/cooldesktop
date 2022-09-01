package com.hxl.desktop.system.core.share

import com.hxl.desktop.common.kotlin.extent.toFile
import com.hxl.desktop.database.CoolDesktopDatabase
import org.apache.commons.io.IOUtils
import org.springframework.beans.factory.annotation.Autowired
import java.net.URLEncoder
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 负责共享文件下载
 */
class CoolDesktopShareHttpServlet : HttpServlet() {
    @Autowired
    lateinit var coolDesktopDatabase: CoolDesktopDatabase
    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        val shareId = req.requestURI.substring(req.requestURI.lastIndexOf("/") + 1)

        val shareLink = coolDesktopDatabase.listShareLink().find { it.shareId == shareId }
        shareLink?.run {
            val file = this.filePath.toFile()
            resp.addHeader("Content-Disposition", "attachment;filename*=UTF-8''${URLEncoder.encode(file.name,"UTF-8")}");
            resp.addHeader("content-length", "${file.length()}");
            IOUtils.copy(this.filePath.toFile().inputStream(), resp.outputStream)
            return
        }
        resp.writer.append("error!")
    }
}