import kotlin.jvm.JvmStatic
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import org.apache.commons.compress.utils.IOUtils
import java.io.*
import java.nio.file.Files

object Java {
    @JvmStatic
    fun main(args: Array<String>) {
    }

    fun unZipFile() {
        // Create zip file stream.
        try {
            ZipArchiveInputStream(
                BufferedInputStream(FileInputStream("/home/HouXinLin/test/test.zip"))
            ).use { archive ->
                var entry: ZipArchiveEntry
                while (archive.nextZipEntry.also { entry = it } != null) {
                    // Print values from entry.
                    println(entry.name)
                    println(entry.method) // ZipEntry.DEFLATED is int 8
                    val file = File("output/" + entry.name)
                    println("Unzipping - $file")
                    // Create directory before streaming files.
                    val dir = file.toPath().toString().substring(0, file.toPath().toString().lastIndexOf("\\"))
                    Files.createDirectories(File(dir).toPath())
                    // Stream file content
                    IOUtils.copy(archive, FileOutputStream(file))
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}