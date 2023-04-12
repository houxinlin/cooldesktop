repositories {
    mavenCentral()
}
dependencies {
    implementation(project(":desktop-file"))
    implementation(project(":desktop-loader"))
    implementation(project(":desktop-database"))
    implementation(project(":desktop-websocket"))
    implementation(project(":desktop-system"))
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

}
tasks.bootJar {
    destinationDirectory.set(file("../bin/cooldesktop"))
    archiveFileName.set("CoolDesktop.jar")
}
tasks.register("install") {
    delete( project.rootDir.toString()+"/bin/jre")
    dependsOn(":desktop-startup:jar")
    dependsOn(":desktop-web:bootJar")

    exec {
        commandLine("/home/LinuxWork/app/developer-apps/java/jdk/jdk-11.0.18/bin/jlink",
            "--no-header-files",
            "--no-man-pages",
            "--compress=2",
            "--strip-debug",
            "--add-modules",
            "java.base,java.desktop,java.logging,java.management,java.naming,java.net.http,java.scripting,java.security.jgss,java.security.sasl,jdk.unsupported",
            "--output",
            project.rootDir.toString()+"/bin/jre")
    }
}
