package com.hxl.desktop.system.core

import com.fasterxml.jackson.databind.ObjectMapper


class WebSocketMessageBuilder {
    class Builder {
        val data = mutableMapOf<String, Any?>()
        fun applySubject(subject: String): Builder {
            data["subject"] = subject
            return this
        }

        fun applyAction(action: String): Builder {
            data["action"] = action
            return this
        }

        fun addItem(key: String, value: Any?): Builder {
            data[key] = value
            return this
        }

        fun build(): String {
            val objectMapper = ObjectMapper()
            return objectMapper.writeValueAsString(data)
        }
    }
}