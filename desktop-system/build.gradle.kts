repositories {
    mavenCentral()
}

dependencies{

    implementation("com.jcraft:jsch:0.1.55")
    implementation(project(":desktop-database"))
// https://mvnrepository.com/artifact/org.jetbrains.pty4j/pty4j
    implementation("org.jetbrains.pty4j:pty4j:0.12.10")



}
