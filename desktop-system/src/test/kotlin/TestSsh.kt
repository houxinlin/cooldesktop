import com.hxl.desktop.system.ssh.SshClient
import com.hxl.desktop.system.ssh.SshClientFactory
import com.hxl.desktop.system.ssh.TerminalOutput

class TestSsh {
}

fun main() {
    var createSshSshClient =
        SshClientFactory().createSshSshClient("root", "houxinlin.com", 22, "hxl495594..", object : TerminalOutput {
            override fun output(data: ByteArray) {
                println(data.decodeToString())
            }
        })

    Thread.sleep(5000)
    createSshSshClient.writeCommand("cd /home")

}