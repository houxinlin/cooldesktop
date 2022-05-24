package com.hxl.desktop.database

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct
import javax.annotation.Resource


@Component
class CoolDesktopDatabase {
    @Resource
    lateinit var sqliteJdbcTemplate: JdbcTemplate

    companion object {
        const val SELECT_SYS_CONFIG_ALL = "select * from sys_config"
        const val SELECT_SYS_PROPERTIES_ALL = "select * from sys_properties"

    }

    @PostConstruct
    private fun init() {
        //创建系统配置表
        sqliteJdbcTemplate.execute("create  table if not exists  sys_config (sys_key TEXT,sys_value TEXT)")
        //创建属性表
        sqliteJdbcTemplate.execute("create table if not exists  sys_properties (sys_key TEXT,sys_value TEXT)")
    }

    fun getSysConfig(key: String): String {
        return listConfigs(SELECT_SYS_CONFIG_ALL).getOrDefault(key, "")
    }

    fun setSysConfigValue(key: String, value: String) {
        save("sys_config", key, value)
    }

    fun getSysProperties(key: String, default: String = ""): String {
        return listConfigs(SELECT_SYS_PROPERTIES_ALL).getOrDefault(key, default)
    }

    fun setSysProperties(key: String, value: String) {
        save("sys_properties", key, value)
    }

    fun setSysProperties(key: String, value: Any) {
        save("sys_properties", key, ObjectMapper().writeValueAsString(value))
    }

    @Synchronized
    private fun save(tableName: String, key: String, value: String) {
        val query = "select count(sys_key) from $tableName where sys_key=?"
        if (sqliteJdbcTemplate.queryForObject(query, Int::class.java, key) > 0) {
            sqliteJdbcTemplate.update("update  $tableName  set sys_value =? where sys_key=?", value, key)
            return
        }
        sqliteJdbcTemplate.update("insert into  $tableName (sys_key, sys_value) values(?, ?)", key, value)
    }

    fun listConfigs(select: String = SELECT_SYS_CONFIG_ALL): MutableMap<String, String> {
        var result = mutableMapOf<String, String>()
        sqliteJdbcTemplate.query(select) { rs, _ ->
            result.put(rs.getString("sys_key"), rs.getString("sys_value"))
        }
        return result
    }
}