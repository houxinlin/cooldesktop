package com.hxl.desktop.system.core

import java.io.IOException
import org.apache.commons.lang3.RandomStringUtils
import org.apache.juli.logging.LogFactory
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths

object TomcatGlobalAuthenticationPasswordUtils {
    private val log = LogFactory.getLog(
        TomcatGlobalAuthenticationPasswordUtils::class.java
    )
    private val PASSWORD_PATH = System.getProperty("user.home") + "/desktop-tomcat"
    private const val PASSWORD_FILE_NAME = "password"
    fun createIfNotExist() {
        try {
            if (!Files.exists(Paths.get(PASSWORD_PATH, PASSWORD_FILE_NAME))) {
                Files.createDirectories(Paths.get(PASSWORD_PATH))
                Files.write(
                    Paths.get(PASSWORD_PATH, PASSWORD_FILE_NAME),
                    randomPassword.toByteArray(StandardCharsets.UTF_8)
                )
            }
        } catch (e: IOException) {
            log.info("无法创建密码文件" + e.message)
        }
    }

    val password: String
        get() {
            createIfNotExist()
            try {
                val bytes = Files.readAllBytes(Paths.get(PASSWORD_PATH, PASSWORD_FILE_NAME))
                return String(bytes)
            } catch (e: IOException) {
                log.info("无法获取密码文件" + e.message)
            }
            return ""
        }
    private val randomPassword: String
        private get() {
            val candidate = "QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm123456789"
            return RandomStringUtils.random(6, candidate)
        }
}