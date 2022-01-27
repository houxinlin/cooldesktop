import net.sf.jmimemagic.Magic
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths


/**
 * @author:   HouXinLin
 * @email:    2606710413@qq.com
 * @date:     20212021/12/18
 * @describe: test
 * @version:  v1.0
 */
object  Test {

    @JvmStatic
    fun main(args: Array<String>) {

    }

    init {
        Magic.initialize()

        println(Magic.getMagicMatch(Files.readAllBytes(Paths.get("/home/HouXinLin/apps/Developer/android-studio-2020.3.1.24-linux.zip"))).mimeType)
    }
}

