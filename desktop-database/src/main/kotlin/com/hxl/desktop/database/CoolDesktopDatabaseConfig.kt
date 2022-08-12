package com.hxl.desktop.database

import com.hxl.desktop.common.core.Directory
import org.h2.jdbcx.JdbcDataSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import java.nio.file.Paths
import javax.sql.DataSource

@Configuration
class CoolDesktopDatabaseConfig {
    companion object {
        const val DATABASE_NAME = "cooldesktop"
    }

    fun createDataSource(): DataSource {
        val databaseDirectory = Directory.getDatabaseDirectory()
        val dbPath = Paths.get(databaseDirectory, DATABASE_NAME)
        return JdbcDataSource().apply { this.setURL( "jdbc:h2:${dbPath}") }
    }

    @Bean
    fun jdbcTemplate(): JdbcTemplate {
        return JdbcTemplate(createDataSource())
    }
}