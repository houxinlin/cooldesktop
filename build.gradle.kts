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
val CooldesktopRoot = layout.projectDirectory.toString()

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }

}
dependencies {

}
repositories {
    mavenCentral()
    flatDir {
        dirs("desktop-lib")
    }
}

tasks.register<Copy>("copyLib") {
    val javaHome = System.getProperty("java.home")
    val parent = File(javaHome).parent
    val toolsPath = "$parent/lib/tools.jar"
    from(
        toolsPath,
          "/home/HouXinLin/projects/java/cool/spring-source-5.1.13/spring-framework/spring-webmvc/build/libs/spring-webmvc-5.3.20-SNAPSHOT.jar",
        "/home/HouXinLin/projects/java/cool/desktop-spring-boot/spring-boot-2.6.1/spring-boot-project/spring-boot/build/libs/spring-boot-2.6.1.jar",
        "/home/HouXinLin/projects/java/cool/desktop-tomcat/apache-tomcat-9.0.58-src/output/embed/tomcat-embed-core.jar",
        "/home/HouXinLin/projects/java/cool/desktop-application-definition/build/libs/desktop-application-definition-1.0-SNAPSHOT.jar",
        "/home/HouXinLin/projects/java/cool/FileMerge/FileMerge/build/libs/FileMerge-1.0-SNAPSHOT.jar",
        "/home/HouXinLin/projects/java/cool/cooldesktop-application-event-definition/cooldesktop-application-event-definition/build/libs/cooldesktop-application-event-definition-1.0-SNAPSHOT.jar"
    )
    into(layout.projectDirectory.dir("desktop-lib"))
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
        exclude(group = "org.springframework", module = "spring-webmvc")

    }
    if (name != "desktop-web") {
        tasks.bootJar {
            enabled = false
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        }

        tasks.jar {
            enabled = true
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        }

    }

    dependencies {
        if (name != "desktop-common") {
            implementation(project(":desktop-common"))
        }

        //tomcat为二次开发的jar，主要功能全局拦截，进行登录，打包的时候会加入，开发的时候使用原本的tomcat
        compileOnly("org.apache.tomcat.embed:tomcat-embed-core:9.0.58")
        runtimeOnly(files("${CooldesktopRoot}/desktop-lib/tomcat-embed-core.jar"))
        implementation("com.squareup.okhttp3:okhttp:4.10.0")
        implementation(files("${CooldesktopRoot}/desktop-lib/tools.jar"))
        implementation(files("${CooldesktopRoot}/desktop-lib/spring-boot-2.6.1.jar"))
        implementation(files("${CooldesktopRoot}/desktop-lib/desktop-application-definition-1.0-SNAPSHOT.jar"))
        implementation(files("${CooldesktopRoot}/desktop-lib/FileMerge-1.0-SNAPSHOT.jar"))
        implementation(files("${CooldesktopRoot}/desktop-lib/spring-webmvc-5.3.20-SNAPSHOT.jar"))
        implementation(files("${CooldesktopRoot}/desktop-lib/cooldesktop-application-event-definition-1.0-SNAPSHOT.jar"))
        implementation("org.springframework.boot:spring-boot-starter-websocket:2.6.2") {
            exclude(group = "org.apache.tomcat.embed", module = "tomcat-embed-core")
        }
        implementation("org.tukaani:xz:1.9")
        implementation("org.apache.commons:commons-compress:1.21")
        implementation("org.springframework.boot:spring-boot-starter-web") {
            exclude(group = "org.apache.tomcat.embed", module = "tomcat-embed-core")
        }
        implementation("org.springframework.boot:spring-boot-starter-aop:2.6.3")
        implementation("net.coobird:thumbnailator:0.4.15")
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
        annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        implementation("org.apache.tika:tika-core:2.3.0")
//        implementation("org.springframework.boot:spring-boot-starter-thymeleaf:2.6.4")
        implementation("org.springframework.boot:spring-boot-loader:2.6.1")
        implementation("org.thymeleaf:thymeleaf:3.1.0.M1")
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
