package com.hxl.desktop.common

import org.springframework.context.ApplicationContext

class GlobalApplication {
    companion object{
        lateinit var applicationContext :ApplicationContext
    }
}