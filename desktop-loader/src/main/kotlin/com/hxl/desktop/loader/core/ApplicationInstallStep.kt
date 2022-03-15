package com.hxl.desktop.loader.core

import com.desktop.application.definition.application.Application
import com.hxl.desktop.common.core.Directory
import com.hxl.desktop.loader.application.ApplicationTypeDetection
import java.nio.ByteBuffer
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

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