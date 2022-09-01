package com.hxl.desktop.database

import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import javax.annotation.Resource

/**
* @description: 创建系统表
* @date: 2022/9/1 上午7:01
*/

@Configuration
class CooldesktopDatabaseTablInitialization :CommandLineRunner {
    @Resource
    lateinit var jdbcTemplate: JdbcTemplate

    override fun run(vararg args: String?) {
        //创建系统配置表
        jdbcTemplate.execute("create table if not exists  ${CoolDesktopDatabase.SYS_CONFIG_TABLE_NAME} (key_name VARCHAR,key_value VARCHAR)")
        //创建属性表
        jdbcTemplate.execute("create table if not exists  ${CoolDesktopDatabase.APP_PROPERTIES_TABLE_NAME} (key_name VARCHAR ,key_value VARCHAR)")
        //创建系统日志表
        jdbcTemplate.execute("create table if not exists  ${CoolDesktopDatabase.SYS_LOG_TABLE_NAME} (id integer   auto_increment ,log_type varchar ,log_level varchar  ,log_name varchar, log_value varchar ,log_time TIMESTAMP,user_name varchar ,ip varchar )")
        //共享文件
        jdbcTemplate.execute("create table if not exists  ${CoolDesktopDatabase.SYS_FILE_SHARE_LINK_MAP} (short_id varchar ,file_path varchar,expir_time TIMESTAMP )")
    }
}