package com.hxl.desktop.database

import org.springframework.jdbc.core.*
import org.springframework.stereotype.Component
import java.sql.ResultSet
import javax.annotation.PostConstruct
import javax.annotation.Resource

@Component
class CoolDesktopDatabase {
    @Resource
    lateinit var sqliteJdbcTemplate: JdbcTemplate

    @PostConstruct
    fun init() {
        sqliteJdbcTemplate.execute("CREATE TABLE if not exists sys_config (sys_key TEXT,sys_value TEXT)")
    }

    fun getSysValue(key: String): String {
        return listConfigs().getOrDefault(key, "")
    }

    @Synchronized
    fun saveConfig(key: String, value: String) {
        if (sqliteJdbcTemplate.queryForObject(
                "select count(sys_key) from sys_config where sys_key=?",
                Int::class.java,
                key
            ) > 0
        ) {
            sqliteJdbcTemplate.update("update  sys_config  set sys_value =? where sys_key=?", value, key)
            return
        }
        sqliteJdbcTemplate.update("INSERT INTO sys_config (sys_key, sys_value) VALUES(?, ?)", key, value)
    }

    fun listConfigs(): MutableMap<String, String> {
        var result = mutableMapOf<String, String>()
        sqliteJdbcTemplate.query("select * from sys_config") { rs, _ ->
            result.put(rs.getString("sys_key"), rs.getString("sys_value"))
        }
        return result
    }
}