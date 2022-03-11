package com.hxl.desktop.common.core

class Constant {
    object StringConstant {
        const val TERMINAL_MESSAGE_CONNECT_NOT_FOUND = "无法打开终端，信息不完整，请到设置界面配置信息"
        const val OK = "OK"
        const val UNINSTALL_FAIL = "卸载失败"
        const val UNINSTALL_SUCCESS = "卸载成功"
        const val CONFIG_FAIL = "配置失败"
        const val CONFIG_FAIL_USER = "配置失败，用户名填写不正确"
        const val SSH_CONNECTION_FAIL = "连接失败，请检查配置"
        var SSH_WRITE_AUTHOR_FAIL =
            "生成密钥成功，但无法写入到authorized_keys文件中，请尝试手动复制\r,系统将在3秒后为您弹出资源目录，请将cooldes.pub中的文件复制到/root/.ssh/authorized_keys中"
    }

    object WebSocketSubjectNameConstant {
        const val REFRESH_FOLDER: String = "/event/file"
        const val TERMINAL_MESSAGE: String = "/event/message/terminal"
        const val REFRESH_WALLPAPER: String = "/event/refresh/wallpaper"
        const val OPEN_DIRECTORY: String = "/event/open/directory"
    }
}