package com.hxl.desktop.common.utils

object ThreadUtils {
    fun createThread(name:String,runnable: Runnable){
        createThread(runnable).start()
    }
    private fun createThread(runnable: Runnable):Thread{
        return Thread(runnable)
    }
}