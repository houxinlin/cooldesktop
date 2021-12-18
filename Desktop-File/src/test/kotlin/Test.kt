import com.hxl.desktop.file.utils.Directory
import java.nio.file.Paths

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
    var get = "asd.a.s."
    get.substring(get.lastIndexOf(".") + 1)?.let {
        println(it.length)
    }
}