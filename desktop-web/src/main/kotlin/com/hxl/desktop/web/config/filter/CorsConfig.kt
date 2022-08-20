package com.hxl.desktop.web.config.filter

import org.apache.catalina.connector.ClientAbortException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.annotation.WebFilter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.math.log

/**
 * @author: HouXinLin
 * @email: 2606710413@qq.com
 * @date: 20212021/12/18
 * @describe:
 * @version: v1.0
 */
@WebFilter(urlPatterns = ["*"])
class CorsConfig :Filter{
    init {

    }
    companion object{
        val log:Logger=LoggerFactory.getLogger(CorsConfig::class.java)
    }
    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        val servletRequest=request as HttpServletRequest;
        val servletResponse=(response as HttpServletResponse)
        servletResponse.addHeader("Access-Control-Allow-Headers","x-requested-with,content-type")
        servletResponse.addHeader("Access-Control-Allow-Methods","GET,POST,PUT,POST")
        servletResponse.addHeader("Access-Control-Allow-Origin",servletRequest.getHeader("Origin"))
        servletResponse.addHeader("Access-Control-Allow-Credentials","true")
        try {
            chain?.doFilter(request,response)
        }catch (e:ClientAbortException){
            log.error(e.message)
        }
    }
}