package com.hxl.desktop.system.manager

import common.extent.toFile
import common.result.FileHandlerResult
import org.springframework.util.FileSystemUtils
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

object ClipboardManager {
    private const val COPY_COMMAND = "copy";
    private const val PASTE_COMMAND = "paste";
    private const val CUT_COMMAND = "cut";
    private const val NONE_COMMAND = "none";
    private var actionCommand = NONE_COMMAND
    private var memoryFilePath: Any? = null;

    fun fileCut(path: String): Boolean {
        if (!path.toFile().exists()) {
            return false
        }
        actionCommand = CUT_COMMAND;
        memoryFilePath = path;
        return true
    }

    fun fileCopy(path: String): Boolean {
        if (!path.toFile().exists()) {
            return false;
        }
        actionCommand = COPY_COMMAND;
        memoryFilePath = path;
        return true
    }

    fun filePaste(path: String): FileHandlerResult {
        try {
            var file = path.toFile()

            if (!file.exists()) {
                return FileHandlerResult.NOT_EXIST
            }
            if (!file.canWrite()) {
                return FileHandlerResult.NO_PERMISSION
            }
            if (actionCommand == NONE_COMMAND) {
                return FileHandlerResult.NO_SELECT_FILE
            }
            return copyToTarget(path, CUT_COMMAND == actionCommand);
        } catch (e: Exception) {
            return FileHandlerResult.create(-1, "", e.message!!)
        }
    }

    private fun copyToTarget(target: String, deleteSource: Boolean): FileHandlerResult {
        memoryFilePath?.let {
            if (it is String
                && Files.isDirectory(Paths.get(target))
                && Files.exists(Paths.get(target))
                && Files.exists(Paths.get(it))
            ) {
                var memoryPath = Paths.get(it)
                var targetPath = Paths.get(target, memoryPath.last().toString())

                var targetFileName = memoryPath.last().toString();
                if (Files.exists(Paths.get(target, targetFileName))) {
                    return FileHandlerResult.TARGET_EXIST
                }

                if (targetPath.startsWith(Paths.get(it))) {
                    return FileHandlerResult.CANNOT_COPY
                }
                FileSystemUtils.copyRecursively(File((it)), targetPath.toFile())
                if (deleteSource) FileSystemUtils.deleteRecursively(File(it))
                return FileHandlerResult.OK
            }
        }
        return FileHandlerResult.TARGET_NOT_EXIST
    }


}