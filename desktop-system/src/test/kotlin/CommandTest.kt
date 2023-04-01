import com.hxl.desktop.common.kotlin.extent.commandExist
import com.hxl.desktop.system.core.command.CommandConstant
import com.hxl.desktop.system.core.command.TerminalCommand
import java.util.stream.Collectors

class CommandTest {
}

fun main() {
    val port: String = if ("lsof".commandExist()) {
        TerminalCommand.Builder()
            .add(CommandConstant.FIND_PROCESS_LISTENER_PORT_BY_LSOF.format("WeChatApp"))
            .execute()
    } else {
        TerminalCommand.Builder()
            .add(CommandConstant.FIND_PROCESS_LISTENER_PORT_BY_NETSTAT.format("WeChatApp"))
            .execute()
    }
    var ports:List<Int> = port.split("\n")
        .stream()
        .map { if (it.isNotBlank() ) it.toIntOrNull()  else null}.filter { it!=null }
        .map { it!!.toInt() }
        .collect(Collectors.toList())
}