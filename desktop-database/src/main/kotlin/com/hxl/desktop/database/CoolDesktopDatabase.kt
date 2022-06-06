package com.hxl.desktop.database

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct
import javax.annotation.Resource


@Component
class CoolDesktopDatabase {
    @Resource
    lateinit var jdbcTemplate: JdbcTemplate

    companion object {
        const val APP_PROPERTIES_TABLE_NAME = "app_properties"
        const val SYS_CONFIG_TABLE_NAME="sys_config"

        const val SELECT_SYS_CONFIG_ALL = "select * from $SYS_CONFIG_TABLE_NAME"
        const val SELECT_APP_PROPERTIES_ALL = "select * from $APP_PROPERTIES_TABLE_NAME"

    }

    @PostConstruct
    private fun init() {
        //创建系统配置表
        jdbcTemplate.execute("create  table if not exists  $SYS_CONFIG_TABLE_NAME (key_name VARCHAR(255),key_value VARCHAR)")
        //创建属性表
        jdbcTemplate.execute("create table if not exists  $APP_PROPERTIES_TABLE_NAME (key_name TEXT,key_value TEXT)")
    }

    /**
     * 获取系统配置
     */
    fun getSysConfig(key: String): String {
        return listConfigs(SELECT_SYS_CONFIG_ALL).getOrDefault(key, "")
    }

    /**
     * 设置系统配置
     */
    fun setSysConfigValue(key: String, value: String) {
        save(SYS_CONFIG_TABLE_NAME , key, value)
    }

    /**
     * 获取程序属性
     */
    fun getAppProperties(key: String, default: String = ""): String {
        return listConfigs(SELECT_APP_PROPERTIES_ALL).getOrDefault(key, default)
    }

    /**
     * 设置程序属性
     */
    fun setAppProperties(key: String, value: String) {
        save(APP_PROPERTIES_TABLE_NAME, key, value)
    }
    /**
     * 设置程序属性，指定对象将被序列化
     */
    fun setAppProperties(key: String, value: Any) {
        save(APP_PROPERTIES_TABLE_NAME, key, ObjectMapper().writeValueAsString(value))
    }

    @Synchronized
    private fun save(tableName: String, key: String, value: String) {
        val query = "select count(key_name) from $tableName where key_name=?"
        if (jdbcTemplate.queryForObject(query, Int::class.java, key) > 0) {
            jdbcTemplate.update("update  $tableName  set key_value =? where key_name=?", value, key)
            return
        }
        jdbcTemplate.update("insert into  $tableName (key_name, key_value) values(?, ?)", key, value)
    }

    fun listConfigs(select: String = SELECT_SYS_CONFIG_ALL): MutableMap<String, String> {
        val result = mutableMapOf<String, String>()
        jdbcTemplate.query(select) { rs, _ ->
            result.put(rs.getString("key_name"), rs.getString("key_value"))
        }
        return result
    }
}