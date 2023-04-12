package com.hxl.desktop;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Startup {
    private static final String DESKTOP_NAME = "/cooldesktop/CoolDesktop.jar";
    private static final String JRE_HOME = "/jre";
    private static final Logger logger = Logger.getLogger(Startup.class.getSimpleName());

    private static Integer toInt(String value) {
        try {
            return Integer.valueOf(value);
        } catch (Exception e) {
        }
        return 0;
    }
    public static void main(String[] args) throws IOException {
        String javaVersion = System.getProperty("java.version");
        String javaHome = System.getProperty("java.home");

        List<Integer> versionList = Arrays.stream(javaVersion.split("\\."))
                .map(Startup::toInt)
                .collect(Collectors.toList());

        File file = new File(Startup.class.getProtectionDomain().getCodeSource().getLocation().getFile());
        String home = file.getParent();

        if (versionList.get(0) < 11) {
            javaHome = Paths.get(home, JRE_HOME).toString();
        }

        Path desktopPath = Paths.get(home, DESKTOP_NAME);
        if (!Files.exists(desktopPath)) {
            logger.info("目录不存在:" + desktopPath);
            System.exit(-1);
        }
        new ProcessBuilder()
                .command(javaHome + "/bin/java", "-jar", desktopPath.toString())
                .redirectOutput(new File(home + "/log/cooldesktop.log"))
                .directory(new File(home))
                .start();
        logger.info("启动成功");
    }
}