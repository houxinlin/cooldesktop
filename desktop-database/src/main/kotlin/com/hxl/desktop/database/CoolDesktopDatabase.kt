package com.hxl.desktop.database

import com.fasterxml.jackson.databind.ObjectMapper
import com.hxl.desktop.common.kotlin.extent.mapToShortArg
import com.hxl.desktop.common.model.Page
import com.hxl.desktop.common.kotlin.extent.toPage
import com.hxl.desktop.database.model.ShareLink
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.sql.ResultSet
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.annotation.PostConstruct
import javax.annotation.Resource


@Component
class CoolDesktopDatabase {
    @Resource
    lateinit var jdbcTemplate: JdbcTemplate

    @Resource
    lateinit var objectMapper: ObjectMapper

    companion object {
        const val LOG_PAGE_SIZE = 50
        const val APP_PROPERTIES_TABLE_NAME = "app_properties"
        const val SYS_LOG_TABLE_NAME = "sys_log"
        const val SYS_CONFIG_TABLE_NAME = "sys_config"
        const val SYS_FILE_SHARE_LINK_MAP = "share_file_map"
        const val SELECT_SYS_CONFIG_ALL = "select * from $SYS_CONFIG_TABLE_NAME"
        const val SELECT_APP_PROPERTIES_ALL = "select * from $APP_PROPERTIES_TABLE_NAME"
        val PERIOD_MAP = mutableMapOf("今天" to 1, "三天内" to 3, "一周内" to 7)

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
        save(SYS_CONFIG_TABLE_NAME, key, value)
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
        save(APP_PROPERTIES_TABLE_NAME, key, objectMapper.writeValueAsString(value))
    }

    @Synchronized
    private fun save(tableName: String, key: String, value: String) {
        jdbcTemplate.update("insert into  $tableName (key_name, key_value) values(?, ?)", key, value)
    }

    fun listConfigs(select: String = SELECT_SYS_CONFIG_ALL): MutableMap<String, String> {
        val result = mutableMapOf<String, String>()
        jdbcTemplate.query(select) { rs, _ -> result.put(rs.getString("key_name"), rs.getString("key_value")) }
        return result
    }

    /**
     * 添加系统日志
     */
    fun addSysLog(logType: String, logLevel: String, logName: String, logValue: String, userName: String = "admin") {
        val time = DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss").format(LocalDateTime.now())
        val ra = RequestContextHolder.getRequestAttributes()
        val ip = if (ra is ServletRequestAttributes) ra.request.remoteAddr else ""
        val insert = "insert into  $SYS_LOG_TABLE_NAME (log_type,log_level,log_name, log_value,log_time,user_name,ip) values(?,?,?,?,?,?,?)"
        jdbcTemplate.update(insert, logType, logLevel, logName, logValue, time, userName, ip)
    }

    /**
     * 删除过期日志(>180天的)
     */
    fun deleteSysExpireLog() {
        jdbcTemplate.update("delete from   $SYS_LOG_TABLE_NAME  where DATEDIFF(DAY,log_time,CURRENT_TIMESTAMP()) >=180")
    }

    /**
     * 列举日志
     */
    fun listSysLog(logType: String, logLevel: String, logFilterTimer: String, page: Int): Page<Map<String, Any>> {
        class SqlCondition {
            private val conditionMap = mutableMapOf<String, Any>()
            private var orderByFieldName: String = ""
            private var orderByType = ""
            fun and(filedName: String, value: Any): SqlCondition {
                conditionMap[filedName] = if (value is String) "\'$value\'" else value
                return this
            }

            fun and(fn: () -> String) {
                conditionMap[fn.invoke().trim()] = fn
            }

            fun orderBy(fieldName: String, type: String) {
                orderByFieldName = fieldName
                orderByType = type
            }

            override fun toString(): String {
                return generator()
            }

            fun generator(): String {
                val result = StringBuffer()
                result.append("where ")
                conditionMap.forEach { (k, v) -> result.append(if (v is kotlin.Function<*>) (v as () -> String).invoke() else "$k=$v and ") }

                return result.removeSuffix("and ")
                    .toString() + if (StringUtils.hasText(orderByFieldName)) " order by $orderByFieldName $orderByType" else ""
            }
        }

        val sqlCondition = SqlCondition()
        sqlCondition.and("log_type", logType)
        if ("全部" != logLevel) sqlCondition.and("log_level", logLevel)
        if ("全部" != logFilterTimer) sqlCondition.and { "DATEDIFF(day, log_time,CURRENT_DATE())<=${PERIOD_MAP.getOrDefault(logFilterTimer, 1)}" }

        sqlCondition.orderBy("id", "desc")
        return jdbcTemplate.queryForList("select * from $SYS_LOG_TABLE_NAME $sqlCondition").toPage(page = page, size = LOG_PAGE_SIZE)
    }

    /**
     * @description: 删除系统分享链接
     * @param id short id
     * @date: 2022/8/30 上午2:54
     */

    fun deleteShareLink(id: String) {
        jdbcTemplate.update("delete from  $SYS_FILE_SHARE_LINK_MAP where short_id=?", id)
    }

    /**
     * @description: 添加系统分享链接
     * @date: 2022/8/30 上午2:53
     */

    fun addShareLink(shortId: String, filePath: String,time:String) {
        jdbcTemplate.update("insert  into  $SYS_FILE_SHARE_LINK_MAP values(?,?,?) ", shortId, filePath,time)
    }


    /**
    * @description: 获取共享链接
    * @date: 2022/9/1 上午7:07
    */

    fun listShareLink():List<ShareLink> {
      val  link: List<ShareLink> =  jdbcTemplate.query("select  * from $SYS_FILE_SHARE_LINK_MAP")
      { rs, _ -> ShareLink(rs.getString("short_id"),rs.getString("file_path"),rs.getString("expir_time")) }
        return link;
    }


    /**
    * @description: 删除过期的共享链接
    * @date: 2022/9/1 上午5:25
    */

    fun deleteSysExpireShareLink() {
        jdbcTemplate.update("delete  from  $SYS_FILE_SHARE_LINK_MAP  where  DATEDIFF(DAY,CURRENT_TIMESTAMP(),expir_time)<=0")
    }
}