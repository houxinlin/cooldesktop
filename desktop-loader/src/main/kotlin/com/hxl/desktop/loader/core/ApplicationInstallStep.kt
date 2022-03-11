package com.hxl.desktop.loader.core

import com.desktop.application.definition.application.Application
import com.hxl.desktop.common.core.Directory
import com.hxl.desktop.loader.application.ApplicationTypeDetection
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

class ApplicationInstallStep(var applicationDownloadManager: ApplicationDownloadManager) : InstallStep<ByteArray?, Boolean> {

    override fun execute(value: ByteArray?): Boolean {
        if (value!=null) {
            var type = ApplicationTypeDetection.detection(value)
            if (type == Application.WEB_MINI_APP) {
                Files.write(Paths.get(Directory.getWebAppDirectory(), createUUIDFileNameString()), value)
                applicationDownloadManager.refreshWebMiniApplication()
                return true
            }
        }
        return false;
    }

    private fun createUUIDFileNameString(): String {
        return "${UUID.randomUUID()}.webapp"
    }
}