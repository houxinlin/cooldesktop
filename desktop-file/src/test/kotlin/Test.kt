import com.hxl.desktop.file.extent.compress
import com.hxl.desktop.file.extent.decompression
import common.extent.toFile
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
        "/home/HouXinLin/test/com/aa.7z".toFile().decompression()
    }

    init {

    }
}

