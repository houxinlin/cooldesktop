package com.hxl.desktop.loader.core

class ApplicationInstallStep(var applicationDownloadManager: ApplicationDownloadManager) :
    InstallStep<List<ByteArray>, Boolean> {

    override fun execute(list: List<ByteArray>): Boolean {
        if (list.isEmpty()) {
            return false
        }
        applicationDownloadManager.refreshProgressState(InstallStep.INSTALLING_STATE)
        list.forEach(applicationDownloadManager::installDispatcher)
        return true;
    }
}