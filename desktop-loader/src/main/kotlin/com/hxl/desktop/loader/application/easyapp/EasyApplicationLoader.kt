package com.hxl.desktop.loader.application.easyapp

import com.desktop.application.definition.application.Application
import com.desktop.application.definition.application.ApplicationInstallState
import com.desktop.application.definition.application.ApplicationLoader
import com.desktop.application.definition.application.UTF8Property
import com.desktop.application.definition.application.easyapp.EasyApplication
import com.hxl.desktop.common.core.Constant
import com.hxl.desktop.common.core.Directory
import com.hxl.desktop.file.extent.listRootDirector
import com.hxl.desktop.loader.application.ApplicationRegister
import com.hxl.desktop.loader.application.ApplicationTypeDetection
import com.hxl.desktop.loader.application.ApplicationWrapper
import com.hxl.desktop.loader.core.ApplicationClassLoader
import com.hxl.desktop.system.core.CoolDesktopBeanRegister
import com.hxl.desktop.system.core.RequestMappingRegister
import com.hxl.desktop.system.core.WebSocketMessageBuilder
import com.hxl.desktop.system.core.WebSocketSender
import com.hxl.desktop.common.extent.toPath
import com.hxl.desktop.common.utils.VersionUtils
import com.hxl.desktop.loader.utils.ApplicationConvertFunction
import com.hxl.desktop.system.config.CoolProperties
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cglib.core.ReflectUtils
import org.springframework.core.io.UrlResource
import org.springframework.core.type.classreading.CachingMetadataReaderFactory
import org.springframework.core.type.filter.AnnotationTypeFilter
import org.springframework.stereotype.Component
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.stream.Collectors
import kotlin.io.path.deleteExisting
import kotlin.io.path.deleteIfExists

@Component
class EasyApplicationLoader : ApplicationLoader<EasyApplication> {
    @Autowired
    private lateinit var applicationRegister: ApplicationRegister

    @Autowired
    private lateinit var requestMappingRegister: RequestMappingRegister

    @Autowired
    private lateinit var webSocketSender: WebSocketSender

    @Autowired
    private lateinit var coolProperties: CoolProperties

    companion object {
        private val log: Logger = LoggerFactory.getLogger(EasyApplicationLoader::class.java)
        private val metadataReaderFactory: CachingMetadataReaderFactory = CachingMetadataReaderFactory()
        const val JAR_FILE_PREFIX = "jar:file:"
        const val FILE_PREFIX = "file:"
        const val CLASS_NAME_SUFFIX = ".class"
        const val APP_PROPERTIES = "app.properties"
        const val JAR_SUFFIX = ".jar"

    }

    override fun support(application: Application): Boolean {
        return application is EasyApplication
    }

    override fun support(byteArray: ByteArray): Boolean {
        return ApplicationTypeDetection.detection(byteArray) == Application.EASY_APP
    }

    override fun loadApplicationFromByte(byteArray: ByteArray): ApplicationInstallState {
        try {
            val tempAppStoragePath =
                Paths.get(Directory.getEasyAppStorageDirectory(), "${UUID.randomUUID()}${JAR_SUFFIX}")
            log.info("存储{}", tempAppStoragePath)
            Files.write(tempAppStoragePath, byteArray)

            //尝试从这个jar中读取信息，可能会失败，主要原因是没有app.properties,或者配置信息不全
            val easyApplication = getApplicationFromFile(JarFile(tempAppStoragePath.toFile()))
            easyApplication?.run {
                //保存不会重复加载
                if (applicationRegister.isLoaded(easyApplication.applicationId)) {
                    tempAppStoragePath.deleteExisting()
                    log.info("无法加载，应用程序已经存在name:{},id:{}", easyApplication.applicationName, easyApplication.applicationId)
                    return ApplicationInstallState.DUPLICATE
                }
                easyApplication.applicationPath = tempAppStoragePath.toString()
                if (Constant.StringConstant.LOAD_APPLICATION_SUCCESS == registerEasyApplication(easyApplication)) {
                    return ApplicationInstallState.INSTALL_OK
                }
            }
            //加载这个应用失败，则删除
            tempAppStoragePath.deleteExisting()
        } catch (e: Exception) {
            log.info(e.message)
        }
        return ApplicationInstallState.INSTALL_FAIL
    }


    override fun loadApplicationFromLocal() {
        val listJarFile = listJarFile()
        listJarFile.forEach { doHandlerLoadJarFile(it) }
    }

    private fun callUninstallMethodIfExist(application: EasyApplication) {
        application.beans.values.forEach {
            try {
                val uninstallMethod = ReflectUtils.findDeclaredMethod(it as Class<*>, "uninstall", arrayOf())
                uninstallMethod?.run { this.invoke(coolDesktopBeanRegister.getBean(it)) }
            } catch (e: NoSuchMethodException) {
            }
        }
    }

    override fun unregisterApplication(application: Application): ApplicationInstallState {
        if (application is EasyApplication) {
            //调用销毁方法
            callUninstallMethodIfExist(application)
            //从spring中销毁bean
            application.beans.values.forEach { coolDesktopBeanRegister.destroyBean(it as Class<*>) }
            //反注册所有Controller
            requestMappingRegister.unregisterApplication(application.applicationId)
            //map中移除这个application
            applicationRegister.unregister(application.applicationId)
            //删除文件
            application.applicationPath.toPath().deleteIfExists()
            return ApplicationInstallState.UNINSTALL_OK

        }
        return ApplicationInstallState.UNINSTALL_FAIL
    }


    /**
     * 获取应用信息从JarFile
     */
    fun getApplicationFromFile(jarFile: JarFile): EasyApplication? {
        val appPropertiesEntry = jarFile.getJarEntry(APP_PROPERTIES)
        appPropertiesEntry?.run {
            val properties = UTF8Property()
            properties.load(jarFile.getInputStream(appPropertiesEntry))
            //如果没有通过属性验证
            if (!Application.checkProperty(properties)) return returnNullAndNotify(jarFile.name, "无法找到属性${Application.findMissProperty(properties)}")
            //根据properties填充一个基本的EasyApplication
            val easyApplication = ApplicationConvertFunction().apply(properties)
            //如果没有通过版本检测
            if (!versionCheck(easyApplication)) return returnNullAndNotify(jarFile.name, "系统版本过低")
            //创建一个完整的EasyApplication
            return createEasyApplication(easyApplication, jarFile)
        }
        return returnNullAndNotify(jarFile.name, "无法找到属性文件")
    }

    private fun createEasyApplication(easyApplication: EasyApplication, jarFile: JarFile): EasyApplication {
        return easyApplication.apply {
            //创建类加载器
            this.classLoader = createClassLoader(jarFile)
            //所有bean
            this.beans = getComponentClassBeanDefinition(this.classLoader, jarFile)
        }
    }

    private fun returnNullAndNotify(appName: String, errorMsg: String): EasyApplication? {
        log.info("无法创建应用，原因:{}", errorMsg)
        notifyClientState(appName, errorMsg)
        return null
    }

    private fun notifyClientState(appName: String, errorMsg: String) {
        webSocketSender.send(
            WebSocketMessageBuilder.Builder()
                .applySubject(Constant.WebSocketSubjectNameConstant.NOTIFY_MESSAGE_ERROR)
                .addItem("data", "无法加载应用${appName}，原因:${errorMsg}")
                .build()
        )
    }

    private fun versionCheck(easyApplication: EasyApplication): Boolean {
        if (VersionUtils.isLz(easyApplication.environmentVersion, coolProperties.coolVersion) == 1) {
            log.warn("${easyApplication.applicationName}无法在本系统上加载，系统版本过低")
            return false
        }
        return true
    }

    fun createClassLoader(rootJar: JarFile): ClassLoader {
        val entries = rootJar.entries()
        val urls = mutableListOf<URL>()
        val springJarFile = org.springframework.boot.loader.jar.JarFile(File(rootJar.name))
        //同时支持jar中的jar
        while (entries.hasMoreElements()) {
            val jarEntries = entries.nextElement()
            if (jarEntries.name.endsWith(JAR_SUFFIX)) {
                val url: URL = springJarFile.getNestedJarFile(springJarFile.getJarEntry(jarEntries.name)).url
                urls.add(url)
            }
        }
        urls.add(URL("${FILE_PREFIX}${rootJar.name}"))
        return ApplicationClassLoader(false, urls.toTypedArray(), EasyApplication::class.java.classLoader)
    }

    /**
     * 获取所有@Componet的class，并且根据classloader实例化
     */
    private fun getComponentClassBeanDefinition(
        classLoader: ClassLoader,
        jarFile: JarFile
    ): MutableMap<String, Any> {
        val componentClassNames = mutableMapOf<String, Any>()

        JarFileClassExtract().extract(jarFile, object : EasyApplicationClassCallback {
            override fun call(urlResource: UrlResource, jarEntry: JarEntry) {
                registerIfComponentClass(urlResource, jarEntry, componentClassNames, classLoader)
            }
        })
        return componentClassNames
    }

    /**
     * 如果是@Component类
     */
    private fun registerIfComponentClass(
        classUrlResource: UrlResource,
        jarEntry: JarEntry,
        componentClassNames: MutableMap<String, Any>,
        classLoader: ClassLoader
    ) {
        val metadataReader = metadataReaderFactory.getMetadataReader(classUrlResource)
        val springBootApplicationAnnotationTypeFilter = AnnotationTypeFilter(SpringBootApplication::class.java)
        if (springBootApplicationAnnotationTypeFilter.match(metadataReader, metadataReaderFactory)) {
            return
        }
        val annotationTypeFilter = AnnotationTypeFilter(Component::class.java)
        //如果是一个@Component类
        if (annotationTypeFilter.match(metadataReader, metadataReaderFactory)) {
            //装换class名称
            val className = jarEntry.name.replace("/", ".").removeSuffix(CLASS_NAME_SUFFIX)
            val beanClass = Class.forName(className, false, classLoader)
            componentClassNames[className] = beanClass
        }
    }

    private fun createApplicationWrapper(application: EasyApplication): ApplicationWrapper {
        return ApplicationWrapper(application)
    }

    /**
     * 系统启动时候执行
     */
    private fun doHandlerLoadJarFile(file: File) {
        val jarFile = JarFile(file.absoluteFile)
        val application = getApplicationFromFile(jarFile)
        application?.run {
            if (applicationRegister.isLoaded(application.applicationId)) {
                webSocketSender.send(
                    WebSocketMessageBuilder.Builder()
                        .applySubject(Constant.WebSocketSubjectNameConstant.NOTIFY_MESSAGE_ERROR)
                        .addItem("data", "无法加载应用${file}")
                        .build()
                )
                log.warn("[{}]已经加载，无法重复加载", application.applicationName)
                return
            }
            application.applicationPath = file.absolutePath
            registerEasyApplication(application)
        }
    }


    /**
     * 对外开放，用来注册EasyApplication，各个地方入口统一走这里
     */
    fun registerEasyApplication(easyApplication: EasyApplication): String {
        // 向Spring中注册Controller，再次之前，需要保证新jar中的class已经在spring容器中
        requestMappingRegister.registerCustomRequestMapping(easyApplication)
        return applicationRegister.registerEasyApplication(createApplicationWrapper(easyApplication))
    }

    @Autowired
    lateinit var coolDesktopBeanRegister: CoolDesktopBeanRegister

    //列觉所有jar
    private fun listJarFile(): List<File> {
        return Directory.getEasyAppStorageDirectory()
            .toPath()
            .listRootDirector()
            .stream()
            .filter { it.name.endsWith(".jar") || it.name.endsWith(".JAR") }
            .collect(Collectors.toList())


    }

}
