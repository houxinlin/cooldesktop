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
import com.hxl.desktop.system.core.CoolDesktopBeanRegister
import com.hxl.desktop.system.core.RequestMappingRegister
import com.hxl.desktop.system.core.WebSocketMessageBuilder
import com.hxl.desktop.system.core.WebSocketSender
import common.extent.toFile
import common.extent.toPath
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.support.ManagedMap
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.core.io.UrlResource
import org.springframework.core.type.classreading.CachingMetadataReaderFactory
import org.springframework.core.type.filter.AnnotationTypeFilter
import org.springframework.stereotype.Component
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.stream.Collectors
import kotlin.io.path.deleteExisting

@Component
class EasyApplicationLoader : ApplicationLoader<EasyApplication> {
    @Autowired
    private lateinit var applicationRegister: ApplicationRegister

    @Autowired
    private lateinit var requestMappingRegister: RequestMappingRegister

    @Autowired
    private lateinit var webSocketSender: WebSocketSender

    override fun support(application: Application): Boolean {
        return application is EasyApplication
    }

    override fun support(byteArray: ByteArray): Boolean {
        return ApplicationTypeDetection.detection(byteArray) == Application.EASY_APP
    }

    override fun loadApplicationFromByte(applicationByte: ByteArray): ApplicationInstallState {
        try {
            val tempAppStoragePath = Paths.get(Directory.getEasyAppStorageDirectory(), "${UUID.randomUUID()}.jar")
            log.info("存储{}", tempAppStoragePath)
            Files.write(tempAppStoragePath, applicationByte)

            //尝试从这个jar中读取信息，可能会失败，主要原因是没有app.properties,或者配置信息不全
            var easyApplication = getApplicationInfoByFile(JarFile(tempAppStoragePath.toFile()))
            easyApplication?.run {
                //保存不会重复加载
                if (applicationRegister.isLoaded(easyApplication.applicationId)) {
                    tempAppStoragePath.deleteExisting()
                    log.info(
                        "无法加载，应用程序已经存在name:{},id:{}",
                        easyApplication.applicationName,
                        easyApplication.applicationId
                    )
                    return ApplicationInstallState.DUPLICATE
                }
                easyApplication.applicationPath = tempAppStoragePath.toString()

                if (Constant.StringConstant.LOAD_APPLICATION_SUCCESS == registerEasyApplication(easyApplication)) {
                    return ApplicationInstallState.INSTALL_OK
                }
            }
            tempAppStoragePath.deleteExisting()
        } catch (e: Exception) {
            log.info(e.message)
        }
        return ApplicationInstallState.INSTALL_FAIL
    }


    override fun loadApplicationFromLocal() {
        val listJarFile = listJarFile()
        listJarFile.forEach { doHandlerJarFile(it) }
    }

    override fun unregisterApplication(application: Application): ApplicationInstallState {
        if (application is EasyApplication) {
            //从spring中销毁bean
            application.beans.values.forEach { coolDesktopBeanRegister.destroyBean(it as Class<*>) }
            //反注册所有Controller
            requestMappingRegister.unregisterApplication(application.applicationId)
            //map中移除这个application
            applicationRegister.unregister(application.applicationId)

            application.applicationPath.toFile().delete()
            return ApplicationInstallState.UNINSTALL_OK

        }
        return ApplicationInstallState.UNINSTALL_FAIL
    }


    private fun createList(data: String, delimiters: String): List<String> {
        return data.split(delimiters)
    }


    private fun createApplicationByProperty(properties: Properties): EasyApplication {
        return EasyApplication().apply {
            this.applicationId = properties.getProperty(Application.APP_ID_PROPERTY_KEY)
            this.applicationName = properties.getProperty(Application.APP_NAME_PROPERTY_KEY)
            this.visibilityIsDesktop =
                properties.getProperty(Application.APP_VISIBILITY_PROPERTY_KEY).lowercase() == "true"
            this.applicationVersion = properties.getProperty(Application.APP_VERSION_PROPERTY_KEY)
            this.author = properties.getProperty(Application.APP_AUTHOR_PROPERTY_KEY)
            this.singleInstance =
                properties.getProperty(Application.APP_SINGLE_INSTANCE_PROPERTY_KEY).lowercase() == "true"
            this.menus = createList(properties.getProperty(Application.APP_AUTHOR_PROPERTY_KEY), ",")
            this.supportMediaTypes = createList(properties.getProperty(Application.APP_SUPPORT_TYPE_KEY), ",")
        }
    }

    //获取应用信息从JarFile
    fun getApplicationInfoByFile(jarFile: JarFile): EasyApplication? {
        val appPropertiesEntry = jarFile.getJarEntry("app.properties")
        if (appPropertiesEntry != null) {
            val properties = UTF8Property()
            properties.load(jarFile.getInputStream(appPropertiesEntry))
            if (Application.checkProperty(properties)) {
                return createApplicationByProperty(properties).apply {
                    this.classLoader = URLClassLoader(arrayOf(URL("file:" + jarFile.name)))
                    this.beans = getComponentClassBeanDefinition(this.classLoader, jarFile)
                }
            }
            webSocketSender.send(
                WebSocketMessageBuilder.Builder()
                    .applySubject(Constant.WebSocketSubjectNameConstant.NOTIFY_MESSAGE_ERROR)
                    .addItem("data", "无法加载应用${jarFile.name}")
                    .build()
            )
            log.info("无法创建应用，原因是无法找到属性，{}", Application.findMissProperty(properties))
            return null
        }
        log.info("无法创建应用，原因是无法找到属性文件，{}", jarFile.name)
        return null;
    }

    //获取所有@Componet的class，并且根据classloader实例化
    private fun getComponentClassBeanDefinition(
        classLoader: ClassLoader,
        jarFile: JarFile
    ): ManagedMap<String, Any> {
        var componentClassNames = ManagedMap<String, Any>()
        JarFileClassExtract().extract(jarFile, object : EasyApplicationClassCallback {
            override fun call(urlResource: UrlResource, jarEntry: JarEntry) {
                registerIfComponentClass(urlResource, jarEntry, componentClassNames, classLoader)
            }
        })
        return componentClassNames
    }

    //如果是@Component类
    private fun registerIfComponentClass(
        classUrlResource: UrlResource,
        jarEntry: JarEntry,
        componentClassNames: MutableMap<String, Any>,
        classLoader: ClassLoader
    ) {
        var metadataReader = metadataReaderFactory.getMetadataReader(classUrlResource)
        var springBootApplicationAnnotationTypeFilter = AnnotationTypeFilter(SpringBootApplication::class.java)
        if (springBootApplicationAnnotationTypeFilter.match(metadataReader, metadataReaderFactory)) {
            return
        }
        var annotationTypeFilter = AnnotationTypeFilter(Component::class.java)
        //如果是一个@Component类
        if (annotationTypeFilter.match(metadataReader, metadataReaderFactory)) {
            //装换class名称
            var className = jarEntry.name.replace("/", ".").removeSuffix(CLASS_NAME_SUFFIX)
            var beanClass = Class.forName(className, false, classLoader)
            var beanName = className
            componentClassNames[beanName] = beanClass
        }
    }

    private fun createApplicationWrapper(application: EasyApplication): ApplicationWrapper {
        return ApplicationWrapper(application)
    }


    //系统启动时候执行
    private fun doHandlerJarFile(file: File) {
        var jarFile = JarFile(file.absoluteFile)
        var application = getApplicationInfoByFile(jarFile)
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

    //对外开放，用来注册EasyApplication，各个地方入口统一走这里
    fun registerEasyApplication(easyApplication: EasyApplication): String {
//        向Spring中注册Controller，再次之前，需要保证新jar中的class已经在spring容器中
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

    companion object {
        private val log: Logger = LoggerFactory.getLogger(EasyApplicationLoader::class.java)
        private val metadataReaderFactory: CachingMetadataReaderFactory = CachingMetadataReaderFactory()
        const val JAR_FILE_PREFIX = "jar:file:"
        const val FILE_PREFIX = "file:"
        const val CLASS_NAME_SUFFIX = ".class"

    }
}
