import com.hxl.desktop.common.extent.toFile
import com.hxl.desktop.file.extent.compress
import com.hxl.desktop.file.extent.decompression
import com.hxl.desktop.file.service.impl.FileServiceImpl


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
    println(FileServiceImpl().fileDecompression("/home/HouXinLin/test/test.tar.xz"))
}
