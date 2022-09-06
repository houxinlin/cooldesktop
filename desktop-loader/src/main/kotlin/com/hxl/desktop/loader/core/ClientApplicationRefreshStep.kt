package com.hxl.desktop.loader.core

class ClientApplicationRefreshStep(private var applicationDownloadManager: ApplicationDownloadManager) :
    InstallStep<Boolean, Any> {

    override fun execute(value: Boolean): Any {
        if (value) {
            applicationDownloadManager.refreshClient()
            return "FAIL"
        }
        applicationDownloadManager.refreshProgressState(InstallStep.INSTALL_FAIL_STATE)
        return "OK"
    }
}