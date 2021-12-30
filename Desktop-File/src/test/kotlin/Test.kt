import com.hxl.desktop.common.extent.listRootDirector
import com.hxl.desktop.common.extent.toFile
import com.hxl.desktop.common.extent.toPath
import com.hxl.desktop.common.extent.walkFileTree
import org.apache.commons.compress.archivers.ArchiveOutputStream
import org.apache.commons.compress.archivers.ArchiveStreamFactory
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import org.apache.commons.compress.utils.IOUtils
import java.io.BufferedInputStream
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.OutputStream
import java.nio.file.*
import java.util.*
import kotlin.io.path.*


/**
 * @author:   HouXinLin
 * @email:    2606710413@qq.com
 * @date:     20212021/12/18
 * @describe: test
 * @version:  v1.0
 */
class Test {

}

fun main() {
    var target = "/home/HouXinLin/test/test/"
    var archiveOutputStream = ArchiveStreamFactory().createArchiveOutputStream(ArchiveStreamFactory.ZIP, FileOutputStream("/home/HouXinLin/test/test.zip"))
    if (!Files.isDirectory(Paths.get(target))) {
        var zipArchiveEntry = ZipArchiveEntry(Paths.get(target).name)
        archiveOutputStream.putArchiveEntry(zipArchiveEntry)
        IOUtils.copy(Files.newInputStream(Paths.get(target)), archiveOutputStream);
        archiveOutputStream.closeArchiveEntry()
        archiveOutputStream.finish()
        archiveOutputStream.close()
        return
    }
//    println(Paths.get(target).listRootDirector())
    addFile(target,archiveOutputStream)

    archiveOutputStream.finish()
    archiveOutputStream.close()

}

fun addFile(target: String, archiveOutputStream: ArchiveOutputStream) {
    for (file in Paths.get(target).walkFileTree()) {
        val entryName: String = file.removePrefix(target)
        val entry = ZipArchiveEntry(entryName)
        if (!file.toPath().isDirectory()) {
            archiveOutputStream.putArchiveEntry(entry)
            IOUtils.copy(file.toFile().inputStream(), archiveOutputStream)
        }else{
            var zipArchiveEntry = ZipArchiveEntry(file.removePrefix(target) + "/")
            archiveOutputStream.putArchiveEntry(zipArchiveEntry)
        }
        archiveOutputStream.closeArchiveEntry()
    }
}