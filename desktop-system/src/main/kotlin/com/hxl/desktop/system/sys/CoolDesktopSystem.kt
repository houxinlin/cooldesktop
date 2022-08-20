package com.hxl.desktop.system.sys

import com.hxl.desktop.common.core.Constant
import com.hxl.desktop.common.core.Directory
import com.hxl.desktop.common.core.log.LogInfosTemplate
import com.hxl.desktop.common.core.log.SystemLogRecord
import com.hxl.desktop.system.ano.NotifyWebSocket
import com.hxl.desktop.common.result.FileHandlerResult
import com.hxl.desktop.database.CoolDesktopDatabase
import com.hxl.desktop.database.CoolDesktopDatabaseConfigKeys
import com.hxl.desktop.system.config.CoolProperties
import com.hxl.desktop.system.config.SystemResourceMvcConfigurer.Companion.WALLPAPER_REQUEST_RESOURCE_PATH
import com.hxl.desktop.system.utils.TomcatGlobalAuthenticationPasswordUtils
import com.hxl.desktop.system.core.WebSocketMessageBuilder
import com.hxl.desktop.system.core.WebSocketSender
import com.hxl.desktop.system.terminal.CommandConstant
import com.hxl.desktop.system.terminal.TerminalCommand
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.util.*
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists

/**
 * CoolDesktop系统管理接口
 */
@Service
class CoolDesktopSystem {

    @Autowired
    lateinit var systemLogRecord: SystemLogRecord

    @Autowired
    lateinit var coolDesktopDatabase: CoolDesktopDatabase

    @Autowired
    lateinit var webSocketSender: WebSocketSender

    @Autowired
    lateinit var coolProperties: CoolProperties

    companion object {
        private val log: Logger = LoggerFactory.getLogger(CoolDesktopSystem::class.java)

        //敏感key不返回客户端
        val SENSITIVE_KEY = arrayOf(
            CoolDesktopDatabaseConfigKeys.SSH_USER_NAME.keyName,
            CoolDesktopDatabaseConfigKeys.SSH_PRIVATE_VALUE.keyName,
            CoolDesktopDatabaseConfigKeys.SSH_PUBLIC_VALUE.keyName
        )
        const val RSA_NAME = "cooldesktop"
        const val AUTHORIZED_KEYS = "/root/.ssh/authorized_keys"
    }

    /**
     * 修改壁纸，任何客户端调用都可以进行壁纸无刷新动态切换
     */
    @NotifyWebSocket(subject = Constant.WebSocketSubjectNameConstant.REFRESH_WALLPAPER, action = "")
    fun changeWallpaper(file: MultipartFile): FileHandlerResult {
        val wallpaperName = "${UUID.randomUUID()}.png"
        file.transferTo(Paths.get(Directory.getWallpaperWorkDirectory(), wallpaperName).toFile())
        val key = CoolDesktopDatabaseConfigKeys.WALLPAPER.keyName
        val oldWallpaperName = coolDesktopDatabase.getSysConfig(key)
        if (oldWallpaperName.isNotEmpty()) {
            Files.deleteIfExists(Paths.get(Directory.getWallpaperWorkDirectory(), oldWallpaperName))
        }
        val value = "${WALLPAPER_REQUEST_RESOURCE_PATH}${wallpaperName}"
        //存储
        coolDesktopDatabase.setSysConfigValue(key, value)
        return FileHandlerResult.create(0, value, Constant.StringConstant.OK)

    }

    /**
     * 获取系统所有配置
     */
    fun getCoolDesktopConfigs(): MutableMap<String, String> {
        val listConfigs = coolDesktopDatabase.listConfigs()
        //排除敏感信息
        SENSITIVE_KEY.forEach { listConfigs.remove(it) }
        listConfigs["cooldesktop.version"] = coolProperties.coolVersion
        return listConfigs
    }

    @Synchronized
    fun configSecureShell(): String {
        //如果已经配置了密钥，则删除
        val privateRsaPath = Paths.get(Directory.getSecureShellConfigDirectory(), RSA_NAME)
        val publicRsaPath = Paths.get(Directory.getSecureShellConfigDirectory(), "${RSA_NAME}.pub")
        val authorizedKeysFile = File(AUTHORIZED_KEYS)
        if (publicRsaPath.exists() && authorizedKeysFile.canRead()) {
            val publicKeyText = publicRsaPath.toFile().readText()
            //如果已经在authorized文件中存在了这一条记录，则删除
            val oldAuthorizedText = authorizedKeysFile.bufferedReader().readText()
            if (oldAuthorizedText.indexOf(publicKeyText) >= 0) {
                val newAuthorizedText = oldAuthorizedText.replace(publicKeyText, "")
                if (authorizedKeysFile.canWrite()) {
                    authorizedKeysFile.writeText(newAuthorizedText)
                }
            }
        }
        privateRsaPath.deleteIfExists()
        publicRsaPath.deleteIfExists()
        //生成公私钥
        TerminalCommand.Builder().add(CommandConstant.SSH_KEYGEN.format(privateRsaPath.toString())).execute()

        if (!privateRsaPath.exists() || !publicRsaPath.exists()) return Constant.StringConstant.CONFIG_FAIL
        val privateRsa = Files.readAllBytes(privateRsaPath)
        val publicRsa = Files.readAllBytes(publicRsaPath)

        coolDesktopDatabase.setSysConfigValue(CoolDesktopDatabaseConfigKeys.SSH_PUBLIC_VALUE.keyName,
            publicRsa.decodeToString())

        coolDesktopDatabase.setSysConfigValue(
            CoolDesktopDatabaseConfigKeys.SSH_PRIVATE_VALUE.keyName,
            privateRsa.decodeToString()
        )

        if (!authorizedKeysFile.canWrite()) {
            systemLogRecord.addLog(LogInfosTemplate.SystemErrorLog("无权限写入文件", AUTHORIZED_KEYS))
            log.info("没有权限写入{}", AUTHORIZED_KEYS)
            webSocketSender.sendForDelay(createDelayMessageToOpenDirectory(privateRsaPath.parent.toString()), "", 3)
            return Constant.StringConstant.SSH_WRITE_AUTHOR_FAIL
        }
        if (!authorizedKeysFile.exists()) authorizedKeysFile.createNewFile()

        Files.write(Paths.get(AUTHORIZED_KEYS), publicRsa, StandardOpenOption.APPEND)
        Files.write(Paths.get(AUTHORIZED_KEYS), "\r".toByteArray(), StandardOpenOption.APPEND)
        return Constant.StringConstant.OK
    }

    fun createDelayMessageToOpenDirectory(data: String): String {
        return WebSocketMessageBuilder.Builder()
            .applySubject(Constant.WebSocketSubjectNameConstant.OPEN_DIRECTORY)
            .addItem("data", data)
            .build()
    }

    fun configSecureShellUser(userName: String): String {
        if (userName.isBlank()) return Constant.StringConstant.CONFIG_FAIL_USER
        coolDesktopDatabase.setSysConfigValue(CoolDesktopDatabaseConfigKeys.SSH_USER_NAME.keyName, userName)
        return Constant.StringConstant.OK
    }

    fun resetLoginPasswd(pass: String): String {
        if (TomcatGlobalAuthenticationPasswordUtils.reset(pass.uppercase())) return Constant.StringConstant.RESET_OK
        return Constant.StringConstant.RESET_FAIL
    }
}