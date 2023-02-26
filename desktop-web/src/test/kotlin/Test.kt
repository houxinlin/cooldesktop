import com.hxl.desktop.system.utils.JarUtils
import org.apache.commons.logging.LogFactory

class Test {
}
fun main(){
    JarUtils.run("/home/ext/project/java/cool/CoolDesktop/desktop-web/build/libs/cooldesktop.jar","-javaagent:/home/HouXinLin/skywalking-agent.jar","","/home/HouXinLin/log.log")
}