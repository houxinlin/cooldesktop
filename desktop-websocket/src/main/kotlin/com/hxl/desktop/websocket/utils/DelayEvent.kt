package com.hxl.desktop.websocket.utils

import java.util.concurrent.Delayed
import java.util.concurrent.TimeUnit

class DelayEvent(val msg: String, val second: Long) : Delayed {
    val expire = System.currentTimeMillis() + (second * 1000)

    override fun compareTo(other: Delayed): Int {
        return (this.getDelay(TimeUnit.MILLISECONDS) - other.getDelay(TimeUnit.MILLISECONDS)) as Int
    }

    override fun getDelay(unit: TimeUnit): Long {
        return unit.convert(this.expire - System.currentTimeMillis(), TimeUnit.MILLISECONDS)
    }
}