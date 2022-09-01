package com.hxl.desktop.common.utils

object ThreadUtils {
    fun createThread(name:String,runnable: Runnable){
        createThread(runnable,name).start()
    }
    private fun createThread(runnable: Runnable,name:String):Thread{
        return Thread(runnable,name)
    }
}