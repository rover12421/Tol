package com.rover12421.tol.util

import org.jboss.shrinkwrap.resolver.api.maven.Maven
import java.io.File
import java.net.URL
import java.net.URLClassLoader

class MavenClassLoaderHelper {
    private val repo = Maven.configureResolver().withMavenCentralRepo(true)
        .withRemoteRepo("huaweicloud", "https://mirrors.huaweicloud.com/repository/maven/", "default")
        .withRemoteRepo("aliyun", "http://maven.aliyun.com/nexus/content/groups/public/", "default")
        .withRemoteRepo("sonatype", "https://oss.sonatype.org/service/local/repositories/releases/content/", "default")
        .withRemoteRepo("jitpack", "https://jitpack.io", "default")

    private val deps = mutableListOf<String>()
    private val urls = mutableListOf<URL>()

    /**
     * 添加仓库
     * 默认已经包含
     * 1. 中央仓库
     * 2. 华为云   https://mirrors.huaweicloud.com/repository/maven/
     * 3. 阿里云   http://maven.aliyun.com/nexus/content/groups/public/
     * 4. sonatype https://oss.sonatype.org/service/local/repositories/releases/content/
     * 5. jitpack https://jitpack.io
     */
    fun addRepo(name: String, url: String) = apply { repo.withRemoteRepo(name, url, "default") }


    /**
     * 添加依赖
     * 例如:
     * junit:junit:4.12
     * org.apache.commons:commons-lang3:3.5
     */
    fun addDependencies(vararg dependencies: String) = apply { dependencies.forEach { deps.add(it) } }

    fun addDepUrls(vararg urls: URL) = apply { urls.forEach { this.urls.add(it) } }

    fun addDepFiles(vararg files: File) = apply { files.forEach { this.urls.add(it.toURI().toURL()) } }

    /**
     * 获取依赖所有文件URL列表
     */
    fun getDependencyUrls(): Array<URL> {
        return try {
            repo.resolve(*deps.toTypedArray())
            addDepFiles(*repo.resolve().withTransitivity().asFile())
            return urls.toTypedArray()
        } catch (e: Throwable) {
            if (!e.localizedMessage.contains("No dependencies were set for resolution")) {
                Log.error("[getDependencyFiles]", e)
            } else {
                Log.warn(e.localizedMessage)
            }
            emptyArray()
        }
    }

    /**
     * 获取依赖所有文件列表
     */
    fun getDependencyFiles(): Array<File> {
        return getDependencyUrls().map { File(it.toURI()) }.toTypedArray()
    }

    /**
     * 获取ClassLoader
     * 每次都是获取全新的ClassLoader
     */
    fun getClassLoader(parentClassLoader: ClassLoader? = Thread.currentThread().contextClassLoader) : URLClassLoader {
        val urls = getDependencyUrls()
        return URLClassLoader(urls, parentClassLoader)
    }
}