repositories {
    mavenCentral()
}
dependencies{
    implementation("net.sf.jmimemagic:jmimemagic:0.1.5")
    implementation("org.apache.tika:tika-core:2.3.0")
    implementation(project(":desktop-database"))
    implementation(project(":desktop-system"))
}
