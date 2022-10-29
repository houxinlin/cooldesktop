import com.hxl.desktop.file.extent.getAttribute
import com.hxl.desktop.file.extent.listRootDirector
import java.nio.file.Paths

/**
 * @author:   HouXinLin
 * @email:    2606710413@qq.com
 * @date:     20212021/12/18
 * @describe: test
 * @version:  v1.0
 */
object  Test {

}

fun main() {
    Paths.get("/home/HouXinLin/temp-files/test").listRootDirector().forEach {
        println(it)
        println(it.getAttribute())
    }
}

