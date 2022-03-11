package com.hxl.desktop.loader.application.easyapp

import com.desktop.application.definition.application.Application
import com.desktop.application.definition.application.ApplicationLoader
import com.desktop.application.definition.application.easyapp.EasyApplication
import com.hxl.desktop.common.core.Directory
import com.hxl.desktop.file.extent.listRootDirector
import com.hxl.desktop.loader.application.ApplicationRegister
import com.hxl.desktop.loader.application.ApplicationWrapper
import common.extent.toFile
import common.extent.toPath
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
import org.springframework.beans.factory.support.BeanNameGenerator
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar
import org.springframework.core.io.UrlResource
import org.springframework.core.type.AnnotationMetadata
import org.springframework.core.type.classreading.CachingMetadataReaderFactory
import org.springframework.core.type.filter.AnnotationTypeFilter
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import java.nio.ByteBuffer
import java.util.*
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.stream.Collectors
import javax.annotation.PostConstruct
import javax.annotation.Resource

@Component
@Import(EasyApplicationImport::class)
class EasyApplicationLoader : ApplicationLoader {
    val metadataReaderFactory: CachingMetadataReaderFactory = CachingMetadataReaderFactory()

    @Autowired
    lateinit var applicationRegister: ApplicationRegister


    @Resource
    lateinit var easyApplicationLoaderCallback: EasyApplicationLoaderCallback
    override fun loadApplication() {
        easyApplicationLoaderCallback.application.forEach(applicationRegister::registerEasyApplication)
    }


}