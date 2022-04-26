package com.hxl.desktop.database

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct
import javax.annotation.Resource

@Component
class CoolDesktopDatabase {
    @Resource
    lateinit var sqliteJdbcTemplate: JdbcTemplate

    companion object {
        const val SELECT_SYS_CONFIG = "select count(sys_key) from sys_config where sys_key=?"
        const val UPDATE_SYS_CONFIG = "update  sys_config  set sys_value =? where sys_key=?"
        const val INSERT_SYS_CONFIG = "INSERT INTO sys_config (sys_key, sys_value) VALUES(?, ?)"
    }

    @PostConstruct
    fun init() {
        sqliteJdbcTemplate.execute("CREATE TABLE if not exists sys_config (sys_key TEXT,sys_value TEXT)")
    }

    fun getSysValue(key: String): String {
        return listConfigs().getOrDefault(key, "")
    }

    @Synchronized
    fun saveConfig(key: String, value: String) {
        if (sqliteJdbcTemplate.queryForObject(SELECT_SYS_CONFIG, Int::class.java, key) > 0) {
            sqliteJdbcTemplate.update(UPDATE_SYS_CONFIG, value, key)
            return
        }
        sqliteJdbcTemplate.update(INSERT_SYS_CONFIG, key, value)
    }

    fun listConfigs(): MutableMap<String, String> {
        var result = mutableMapOf<String, String>()
        sqliteJdbcTemplate.query("select * from sys_config") { rs, _ ->
            result.put(rs.getString("sys_key"), rs.getString("sys_value"))
        }
        return result
    }
}