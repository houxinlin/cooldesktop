package com.hxl.desktop.common.core

class Constant {
    object StringConstant {
        const val TERMINAL_MESSAGE_CONNECT_NOT_FOUND = "无法打开终端，信息不完整，请到设置界面配置信息"
        const val OK = "OK"
        const val NO_PERMISSION = "无权限操作"
        const val UNINSTALL_FAIL = "卸载失败"
        const val NOT_FOUND_LOADERS = "找不到加载器，无法卸载"
        const val UNINSTALL_SUCCESS = "卸载成功"
        const val CONFIG_FAIL = "配置失败"
        const val CONFIG_FAIL_USER = "配置失败，用户名填写不正确"
        const val SSH_CONNECTION_FAIL = "连接失败，请检查配置"
        const val LOAD_APPLICATION_SUCCESS = "应用加载成功"
        const val LOAD_APPLICATION_DUPLICATE = "无法重复加载相同ID应用"
        const val LOAD_APPLICATION_FAIL = "加载应用失败"
        var SSH_WRITE_AUTHOR_FAIL =
            "生成密钥成功，但无法写入到authorized_keys文件中，请尝试手动复制\r,系统将在3秒后为您弹出资源目录，请将cooldes.pub中的文件复制到/root/.ssh/authorized_keys中"
    }

    object WebSocketSubjectNameConstant {
        const val APPLICATION_PROGRESS: String = "/event/install/progress"
        const val REFRESH_FOLDER: String = "/event/file"
        const val TERMINAL_MESSAGE: String = "/event/message/terminal"
        const val REFRESH_WALLPAPER: String = "/event/refresh/wallpaper"
        const val REFRESH_APPLICATION: String = "/event/refresh/application"
        const val OPEN_DIRECTORY: String = "/event/open/directory"
        const val COMPRESS_RESULT: String = "/event/compress/result"
        const val NOTIFY_MESSAGE: String = "/event/notify/message"
    }

    object ApplicationEvent {
        const val ACTION_REFRESH_WEB_MINI_APPLICATION = 1
    }
}