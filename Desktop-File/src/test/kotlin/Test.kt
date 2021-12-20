import com.hxl.desktop.file.service.impl.FileServiceImpl
import com.hxl.desktop.file.utils.Directory
import org.springframework.util.FileSystemUtils
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import kotlin.io.path.createDirectories
import kotlin.io.path.deleteExisting
import kotlin.io.path.getOwner

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
    var name ="/home/HouXinLin/project/java/CoolDesktop/work/chunk/da01960c-9dfa-4f8e-b1ff-50d8d0c96d74/all/l";

    Paths.get(name).createDirectories()
}