repositories {
    mavenCentral()
}

dependencies{
    // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-websocket
    implementation("org.springframework.boot:spring-boot-starter-websocket:2.6.2")

    implementation(project(":desktop-system"))
}
