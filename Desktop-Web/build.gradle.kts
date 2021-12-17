repositories {
    mavenCentral()
}

dependencies{
    implementation(project(":Desktop-File"))
    implementation(project(":Desktop-Loader"))
}
tasks.bootJar{
    enabled=false
}

tasks.jar{
    enabled=true
}
