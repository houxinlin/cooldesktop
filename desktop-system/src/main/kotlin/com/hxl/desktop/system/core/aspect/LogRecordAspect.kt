package com.hxl.desktop.system.core.aspect

import com.fasterxml.jackson.databind.ObjectMapper
import com.hxl.desktop.common.core.log.LogInfos
import com.hxl.desktop.common.core.log.LogInfosTemplate
import com.hxl.desktop.common.core.log.SystemLogRecord
import com.hxl.desktop.common.core.log.enums.CoolDesktopLogInfoType
import com.hxl.desktop.common.core.log.enums.CoolDesktopLogType
import com.hxl.desktop.database.CoolDesktopDatabase
import com.hxl.desktop.system.ano.LogRecord
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import kotlin.math.log

/**
 * 日志记录
 */
@Aspect
@Component
class LogRecordAspect {
    @Autowired
    lateinit var coolDesktopDatabase: CoolDesktopDatabase

    @Autowired
    lateinit var logRecored: SystemLogRecord

    companion object {
        private val RECORD_CONVERTERS = arrayOf(BaseDataRecord(), MultipartFileRecord(), ObjectRecord())
    }

    @Pointcut(value = "@annotation(com.hxl.desktop.system.ano.LogRecord)")
    private fun logPointcut() {
    }

    @Before("logPointcut()")
    fun notifyAfterReturning(joinPoint: JoinPoint) {
        val logValueMap = mutableMapOf<String, String>()
        val signature = joinPoint.signature as MethodSignature
        val method = signature.method

        for (i in joinPoint.args.indices) {
            val paramName = method.parameters[i].name
            for (recordConverter in RECORD_CONVERTERS) {
                if (recordConverter.support(joinPoint.args[i])) {
                    logValueMap[paramName] = recordConverter.converter(joinPoint.args[i])
                    break
                }

            }
        }
        val logRecord = signature.method.getDeclaredAnnotation(LogRecord::class.java)
        logRecored.addLog(LogInfosTemplate.ApiInfoLog(logRecord.logName, logValueMap.toString()))
    }

    interface IRecord {
        fun support(param: Any): Boolean
        fun converter(param: Any): String
    }

    class BaseDataRecord : IRecord {
        val BASE_DATA_TYPE = arrayOf(String::class, Int::class, Float::class, Double::class)
        override fun support(param: Any): Boolean {
            return BASE_DATA_TYPE.contains(param::class)
        }

        override fun converter(param: Any): String {

            return param.toString()
        }
    }

    class MultipartFileRecord : IRecord {
        override fun support(param: Any): Boolean {
            return param is MultipartFile
        }

        override fun converter(param: Any): String {
            return (param as MultipartFile).originalFilename ?: return ""
        }
    }

    class ObjectRecord : IRecord {
        override fun support(param: Any): Boolean {
            return true
        }

        override fun converter(param: Any): String {
            try {
                return ObjectMapper().writeValueAsString(param)
            } catch (ex: Exception) {
            }
            return ""
        }
    }
}