package com.hxl.desktop.system.core

import com.alibaba.fastjson.JSON

class WebSocketMessageBuilder {
    fun builder(): Builder {
        return Builder()
    }

    class Builder {
        var data = mutableMapOf<String, Any>()
        fun applySubject(subject: String): Builder {
            data["subject"] = subject
            return this
        }
        fun applyAction(action: String): Builder {
            data["action"] = action
            return this
        }
        fun addItem(key: String, value: Any): Builder {
            data[key] = value
            return this
        }

        fun build(): String {
            return JSON.toJSONString(data)
        }
    }
}