package com.hxl.desktop.loader.cache

abstract class ResourceCache {
    private var map = mutableMapOf<String, ByteArray>()

    fun addCacheResource(name: String, resource: ByteArray) {
        map[name] = resource
    }

    fun inCache(name: String): Boolean {
        return map.containsKey(name)
    }

    fun getCacheResource(name: String): ByteArray? {
        return map[name]
    }

    fun destory() {
        map.clear()
    }

    abstract fun init()
}