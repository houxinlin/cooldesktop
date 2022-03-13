package com.hxl.desktop.loader.core

class ClientApplicationRefreshStep(var applicationDownloadManager: ApplicationDownloadManager) :
    InstallStep<Boolean, Any> {

    override fun execute(value: Boolean): Any {
        applicationDownloadManager.refreshClient()
        return "OK"
    }
}