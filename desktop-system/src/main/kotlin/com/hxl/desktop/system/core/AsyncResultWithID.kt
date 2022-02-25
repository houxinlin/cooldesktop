package com.hxl.desktop.system.core

import org.springframework.scheduling.annotation.AsyncResult
import java.util.*

class AsyncResultWithID<V>(var v: V, var taskId: String) : AsyncResult<V>(v) {

}