package com.hxl.desktop.database

import com.hxl.desktop.common.core.Directory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import org.sqlite.SQLiteDataSource
import java.nio.file.Paths
import javax.annotation.Resource
import javax.sql.DataSource
import kotlin.io.path.exists

@Configuration(proxyBeanMethods = false)
class CoolDesktopDatabaseConfig {
    companion object {
        const val DATABASE_NAME = "cooldesktop.db"
    }

    fun sqliteDataSource(): DataSource {
        var databaseDirectory = Directory.getDatabaseDirectory()
        var dbPath = Paths.get(databaseDirectory, DATABASE_NAME)
        return SQLiteDataSource().apply {
            this.url = "jdbc:sqlite:${dbPath}"
            this.databaseName = dbPath.toString()
        }
    }

    @Bean
    fun sqliteJdbcTemplate(): JdbcTemplate {
        return JdbcTemplate(sqliteDataSource())
    }
}