package com.hxl.desktop.loader.application.easyapp

import com.desktop.application.definition.application.Application
import com.desktop.application.definition.application.easyapp.EasyApplication
import com.hxl.desktop.common.core.Directory
import com.hxl.desktop.common.core.UTF8Property
import com.hxl.desktop.file.extent.listRootDirector
import com.hxl.desktop.loader.application.ApplicationRegister
import com.hxl.desktop.loader.application.ApplicationWrapper
import common.extent.toFile
import common.extent.toPath
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.support.BeanDefinitionBuilder
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.BeanNameGenerator
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar
import org.springframework.core.io.UrlResource
import org.springframework.core.type.AnnotationMetadata
import org.springframework.core.type.classreading.CachingMetadataReaderFactory
import org.springframework.core.type.filter.AnnotationTypeFilter
import org.springframework.stereotype.Component
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import java.nio.ByteBuffer
import java.util.*
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.stream.Collectors
import javax.annotation.PostConstruct

class EasyApplicationImport : ImportBeanDefinitionRegistrar {
    private val metadataReaderFactory: CachingMetadataReaderFactory = CachingMetadataReaderFactory()

    private val easyApplication = arrayListOf<ApplicationWrapper>()

    override fun registerBeanDefinitions(importingClassMetadata: AnnotationMetadata, registry: BeanDefinitionRegistry) {
        var beanDefinitionBuilder = BeanDefinitionBuilder
            .genericBeanDefinition(EasyApplicationLoaderCallback::class.java)
            .addConstructorArgValue(easyApplication)

        val beanName = BeanDefinitionReaderUtils.generateBeanName(beanDefinitionBuilder.beanDefinition, registry)
        registry.registerBeanDefinition(beanName, beanDefinitionBuilder.beanDefinition)
        var jarFiles = listJarFile()
        log.info("Easy App列表{}", jarFiles)
        jarFiles.forEach { doHandlerJarFile(it, registry) }
    }


    companion object {
        const val JAR_FILE_PREFIX = "jar:file:"
        const val FILE_PREFIX = "file:"
        const val CLASS_NAME_SUFFIX = ".class"
        private val log = LoggerFactory.getLogger(EasyApplicationLoader::class.java)
    }

    private val urlClassLoader: URLClassLoader

    init {
        var jarFiles =
            Directory.getEasyAppStorageDirectory().toFile().listFiles()
        var jarFileUls =
            jarFiles.toList().stream().map { URL(FILE_PREFIX + it.absolutePath) }.collect(Collectors.toList())
        urlClassLoader = URLClassLoader(jarFileUls.toTypedArray())
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

    //有没有配置信息文件
    private fun getApplicationInfoByFile(jarFile: JarFile): EasyApplication? {
        var appPropertiesEntry = jarFile.getJarEntry("app.properties")
        if (appPropertiesEntry != null) {
            var properties = UTF8Property()
            properties.load(jarFile.getInputStream(appPropertiesEntry))
            if (Application.checkProperty(properties)) {
                return createApplicationByProperty(properties)
            }
            log.info("无法创建应用，原因是无法找到属性，{}", Application.findMissProperty(properties))
            return null
        }
        log.info("无法创建应用，原因是无法找到属性文件，{}", jarFile.name)
        return null;
    }

    private fun doHandlerJarFile(jarFile: File, registry: BeanDefinitionRegistry) {
        var jarFile = JarFile(jarFile.absoluteFile)
        var application = getApplicationInfoByFile(jarFile)

        var componentClassNames = mutableListOf<String>()
        //如果根目录存在app属性文件
        if (application != null) {
            //尝试注册component
            jarFile.stream().forEach {
                //如果是class文件
                if (it.name.endsWith(CLASS_NAME_SUFFIX)) {
                    var path = JAR_FILE_PREFIX + jarFile.name + "!/" + it.name;
                    var classUrlResource = UrlResource(path)
                    var byteArray = ByteArray(4)
                    //读取前4个字节
                    classUrlResource.inputStream.read(byteArray)
                    //如果是class类型
                    if (isClass(byteArray)) {
                        registerIfComponentClass(classUrlResource, registry, it, jarFile, componentClassNames)
                    }

                }
            }
            if (componentClassNames.isNotEmpty()) {
                easyApplication.add(createApplicationWrapper(application))
            }
        }


    }

    private fun registerIfComponentClass(
        classUrlResource: UrlResource,
        registry: BeanDefinitionRegistry,
        jarEntry: JarEntry,
        jarFile: JarFile,
        componentClassNames: MutableList<String>
    ) {
        var metadataReader = metadataReaderFactory.getMetadataReader(classUrlResource)
        var annotationTypeFilter = AnnotationTypeFilter(Component::class.java)
        //如果是一个@Component类
        if (annotationTypeFilter.match(metadataReader, metadataReaderFactory)) {
            //装换class名称
            var className = jarEntry.name.replace("/", ".").removeSuffix(CLASS_NAME_SUFFIX)
            var beanDefinition =
                BeanDefinitionReaderUtils.createBeanDefinition(null, className, urlClassLoader)
            log.info("注册API，{},{}", jarFile.name, className)
            var beanName = BeanDefinitionReaderUtils.generateBeanName(beanDefinition, registry)
            registry.registerBeanDefinition(beanName, beanDefinition)
            componentClassNames.add(className)
        }
    }

    private fun createApplicationWrapper(application: EasyApplication): ApplicationWrapper {
        return ApplicationWrapper(application)
    }

    //是否是class文件
    fun isClass(byteArray: ByteArray): Boolean {
        var wrap = ByteBuffer.wrap(byteArray)
        return wrap.limit() == 4 && (wrap.getInt(0) == -889275714)
    }

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