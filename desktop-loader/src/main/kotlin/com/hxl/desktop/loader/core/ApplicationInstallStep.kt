package com.hxl.desktop.loader.core

class ApplicationInstallStep(var applicationDownloadManager: ApplicationDownloadManager) :
    InstallStep<List<ByteArray>, Boolean> {

    override fun execute(value: List<ByteArray>): Boolean {
        if (value.isEmpty()) {
            return false
        }
        applicationDownloadManager.refreshProgressState(InstallStep.INSTALLING_STATE)
        value.forEach(applicationDownloadManager::installDispatcher)
        return true;
    }
}