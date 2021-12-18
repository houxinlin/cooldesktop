package com.hxl.desktop.web.app.web

import com.hxl.desktop.file.utils.Directory
import com.hxl.desktop.web.extent.asHttpResponseBody
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

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
    @GetMapping("/list")
    fun list(@RequestParam("root") root: String): Any {
        return Directory.listDirector(root).asHttpResponseBody();
    }

}