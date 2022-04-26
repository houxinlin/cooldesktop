package com.hxl.desktop.system.core

import org.springframework.scheduling.annotation.AsyncResult

class AsyncResultWithID<V>(var v: V, var taskId: String) : AsyncResult<V>(v) {

}