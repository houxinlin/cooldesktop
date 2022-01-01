import com.hxl.desktop.common.extent.toFile
import com.hxl.desktop.common.extent.toPath

import com.hxl.desktop.file.compress.FileCompress
import com.hxl.desktop.file.extent.getAttribute
import org.apache.commons.compress.archivers.ArchiveOutputStream
import org.apache.commons.compress.archivers.ArchiveStreamFactory
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile
import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream
import org.apache.commons.compress.compressors.CompressorStreamFactory
import org.apache.commons.compress.compressors.xz.XZCompressorOutputStream
import org.apache.commons.compress.utils.IOUtils
import java.io.File
import java.io.FileOutputStream
import java.nio.file.*
import java.nio.file.attribute.AclFileAttributeView
import java.nio.file.attribute.BasicFileAttributeView
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.FileTime
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


    var target = "/home/HouXinLin/test/srchttp___bpic.588ku.com_back_pic_05_81_35_655c35589447c67.jpg&referhttp___bpic.588ku.jpeg"
    var readAttributes = Files.getFileAttributeView(target.toPath(), BasicFileAttributeView::class.java).readAttributes()

    println(target.toPath().exists())



//
//    var out = SevenZOutputFile(File("/home/HouXinLin/test/test/test.7z"))
//
//    var sevenZArchiveEntry = SevenZArchiveEntry()
//    sevenZArchiveEntry.name = "list"
//    out.putArchiveEntry(out.createArchiveEntry("/home/HouXinLin/test/test/list/a.txt".toFile(), "a"))
//
//    out.write("/home/HouXinLin/test/test/list/a.txt".toFile().readBytes())
//    out.closeArchiveEntry()
//    out.finish()

//    val gzippedOut = CompressorStreamFactory()
//        .createCompressorOutputStream(CompressorStreamFactory.XZ, FileOutputStream("/home/HouXinLin/test/test.tar"))
//
//    var archiveOutputStream = ArchiveStreamFactory().createArchiveOutputStream(ArchiveStreamFactory.TAR, gzippedOut)
//
//    if (!Files.isDirectory(Paths.get(target))) {
//        var tarArchiveEntry = TarArchiveEntry(Paths.get(target).name)
//        tarArchiveEntry.size = Files.size(Paths.get(target))
//        archiveOutputStream.putArchiveEntry(tarArchiveEntry)
//        IOUtils.copy(Files.newInputStream(Paths.get(target)), archiveOutputStream);
//        archiveOutputStream.closeArchiveEntry()
//        archiveOutputStream.finish()
//        archiveOutputStream.close()
//        return
//    }
//    addFile(target, archiveOutputStream)
//    archiveOutputStream.finish()
//    archiveOutputStream.close()

}

