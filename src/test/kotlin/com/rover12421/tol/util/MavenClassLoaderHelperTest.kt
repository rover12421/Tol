package com.rover12421.tol.util

import java.nio.file.Files
import java.nio.file.Paths
import kotlin.test.Test

class MavenClassLoaderHelperTest {
    @Test
    fun `test load class from maven dependency`() {
        val mavenClassLoaderHelper = MavenClassLoaderHelper()
        mavenClassLoaderHelper.addDependencies(
            "org.apache.commons:commons-lang3:3.12.0",
            "oro:oro:2.0.8",
            "javax.annotation:javax.annotation-api:1.3.2"
        )
        mavenClassLoaderHelper.getDependencyFiles().forEach { file ->
            val size = Files.size(Paths.get(file.toURI()))
            println("$file size: $size")
        }
    }

}