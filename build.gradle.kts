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

dependencies {

}
repositories {
    mavenCentral()
}

subprojects {
    apply {
        apply {
            plugin("io.spring.dependency-management")
            plugin("org.springframework.boot")
            plugin("org.jetbrains.kotlin.plugin.spring")
            plugin("org.jetbrains.kotlin.jvm")
        }

    }
    configurations.implementation {
        exclude(group = "org.springframework.boot", module = "spring-boot")
    }
    if (name != "desktop-web") {
        tasks.bootJar {
            enabled = false

        }

        tasks.jar {
            enabled = true
        }

    }

    dependencies {
        if (name != "desktop-common") {
            implementation(project(":desktop-common"))
        }
        //tomcat为二次开发的jar，主要功能全局拦截，进行登录，打包的时候会加入，开发的时候使用原本的tomcat
        compileOnly("org.apache.tomcat.embed:tomcat-embed-core:9.0.58")
        runtimeOnly(files("/home/HouXinLin/project/java/tomcat/desktop-tomcat/apache-tomcat-9.0.58-src/output/embed/tomcat-embed-core.jar"))


        //SpringBoot为二次开发的jar，主要用来提供结构加载第三方应用
        implementation(files("/home/HouXinLin/project/java/desktop-spring-boot/spring-boot-2.6.1/spring-boot-project/spring-boot/build/libs/spring-boot-2.6.1.jar"))

        //文件合并
        implementation(files("/home/HouXinLin/project/java/FileMerge/FileMerge/build/libs/FileMerge-1.0-SNAPSHOT.jar"))
        //应用程序定义
        implementation(files("/home/HouXinLin/project/java/desktop-application-definition/build/libs/desktop-application-definition-1.0-SNAPSHOT.jar"))
        
        implementation("org.springframework.boot:spring-boot-starter-websocket:2.6.2")
        implementation("org.tukaani:xz:1.9")
        implementation("org.apache.commons:commons-compress:1.21")
        implementation("org.springframework.boot:spring-boot-starter-web")
        implementation("org.springframework.boot:spring-boot-starter-aop:2.6.3")
        implementation("net.coobird:thumbnailator:0.4.15")
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
        annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
        testImplementation("org.springframework.boot:spring-boot-starter-test")

        implementation("com.alibaba:fastjson:1.2.79")
        implementation("org.apache.commons:commons-lang3:3.12.0")

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
