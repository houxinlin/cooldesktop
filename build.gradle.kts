import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.6.1"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.6.0"
    kotlin("plugin.spring") version "1.6.0"
}

group = "com.hxl"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

dependencies{

}
repositories {
    mavenCentral()
}

subprojects{
    apply {
        apply{
            plugin("io.spring.dependency-management")
            plugin("org.springframework.boot")
            plugin("org.jetbrains.kotlin.plugin.spring")
            plugin("org.jetbrains.kotlin.jvm")
        }

    }

    if(name!="desktop-web"){
        tasks.bootJar {
            enabled = false

        }

        tasks.jar {
            enabled = true
        }

    }

    dependencies {
        if (name!="desktop-common"){
            implementation(project(":desktop-common"))
        }
        implementation("org.tukaani:xz:1.9")
        implementation("org.apache.commons:commons-compress:1.21")

        implementation("org.springframework.boot:spring-boot-starter-web"){
//            exclude(group="org.apache.tomcat.embed",module = "tomcat-embed-core")
        }
        implementation("net.coobird:thumbnailator:0.4.15")
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
        annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        implementation("com.alibaba:fastjson:1.2.79")
        implementation(files("/home/HouXinLin/project/java/FileMerge/FileMerge/build/libs/FileMerge-1.0-SNAPSHOT.jar"))

    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "1.8"
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}
