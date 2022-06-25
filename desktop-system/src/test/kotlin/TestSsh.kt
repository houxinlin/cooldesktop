import com.hxl.desktop.system.tail.Tail

class TestSsh {
}

fun main() {
    Tail {
        println(it)
    }.start("/home/HouXinLin/log.out")
}
