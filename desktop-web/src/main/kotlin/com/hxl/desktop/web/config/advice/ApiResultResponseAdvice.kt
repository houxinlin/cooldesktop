package com.hxl.desktop.web.config.advice

import com.fasterxml.jackson.databind.ObjectMapper
import com.hxl.desktop.common.extent.asHttpResponseBody
import com.hxl.desktop.system.ano.UnifiedApiResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.MethodParameter
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice
import java.util.concurrent.Future

/**
 * 统一进行结果封装
 */
@RestControllerAdvice
class ApiResultResponseAdvice : ResponseBodyAdvice<Any> {
    @Autowired
    lateinit var objectMapper: ObjectMapper

    override fun supports(returnType: MethodParameter, converterType: Class<out HttpMessageConverter<*>>): Boolean {
        val resultResponse: UnifiedApiResult? = returnType.getMethodAnnotation(UnifiedApiResult::class.java)
            ?: AnnotationUtils.findAnnotation(returnType.containingClass, UnifiedApiResult::class.java)
        return resultResponse != null
    }

    override fun beforeBodyWrite(
        body: Any?,
        returnType: MethodParameter,
        selectedContentType: MediaType,
        selectedConverterType: Class<out HttpMessageConverter<*>>,
        request: ServerHttpRequest,
        response: ServerHttpResponse
    ): Any? {
        if (body is Resource) return body
        if (body is Future<*>) return body
        //如果是String类型，不能返回其他类型数据,直接把它封装为HttpResponseBody
        if (body is String) return objectMapper.writeValueAsString(body.asHttpResponseBody())
        return body?.asHttpResponseBody()
    }
}