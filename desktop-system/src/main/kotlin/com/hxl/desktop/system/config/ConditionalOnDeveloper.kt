package com.hxl.desktop.system.config

import org.springframework.context.annotation.Condition
import org.springframework.context.annotation.ConditionContext
import org.springframework.core.type.AnnotatedTypeMetadata

/**
 * 只在开发的时候使用
 */
class ConditionalOnDeveloper : Condition {
    override fun matches(context: ConditionContext, metadata: AnnotatedTypeMetadata): Boolean {
        return ConditionalOnDeveloper::class.java.getResource("")?.protocol =="file"
    }
}