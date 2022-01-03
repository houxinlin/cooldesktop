package com.hxl.desktop.common.manager

import com.hxl.desktop.common.result.FileHandlerResult
import org.apache.tomcat.jni.Directory
import org.springframework.util.FileSystemUtils
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.isDirectory

object ClipboardManager {
    const val COPY_COMMAND = "copy";
    const val PASTE_COMMAND = "paste";
    const val CUT_COMMAND = "cut";
    const val NONE_COMMAND = "none";
    var actionCommand = NONE_COMMAND
    var memoryFilePath: Any? = null;

    fun fileCut(path: String) {
        actionCommand = CUT_COMMAND;
        memoryFilePath = path;
    }

    fun fileCopy(path: String) {
        actionCommand = COPY_COMMAND;
        memoryFilePath = path;
    }

    private fun copyToTarget(target: String, deleteSource: Boolean): FileHandlerResult {
        memoryFilePath?.let {
            if (it is String
                    && Files.isDirectory(Paths.get(target))
                    && Files.exists(Paths.get(target))
                    && Files.exists(Paths.get(it))) {
                var memoryPath = Paths.get(it)
                var targetPath = Paths.get(target, memoryPath.last().toString())

                var targetFileName = memoryPath.last().toString();
                if (Files.exists(Paths.get(target, targetFileName))) {
                    return FileHandlerResult.TARGET_EXIST
                }
                if (memoryPath.isDirectory()) {
                    if (targetPath.startsWith(Paths.get(it))){
                        return FileHandlerResult.CANNOT_COPY
                    }
                    FileSystemUtils.copyRecursively(File((it)), targetPath.toFile())
                    if (deleteSource) FileSystemUtils.deleteRecursively(File(it))
                    return FileHandlerResult.OK
                }
                Files.copy(memoryPath, targetPath)
                if (deleteSource) FileSystemUtils.deleteRecursively(File(it))
                return FileHandlerResult.OK
            }
        }
        return FileHandlerResult.TARGET_NOT_EXIST
    }

    fun filePaste(path: String): FileHandlerResult {
        if (actionCommand == COPY_COMMAND) {
            return copyToTarget(path, false);
        }
        if (actionCommand == CUT_COMMAND) {
            return copyToTarget(path, true);
        }
        return FileHandlerResult.NO_SELECT_FILE
    }
}