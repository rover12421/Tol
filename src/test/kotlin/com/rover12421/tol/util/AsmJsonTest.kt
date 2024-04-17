package com.rover12421.tol.util

import com.rover12421.tol.asm.util.AsmJsonUtil
import java.nio.file.Paths
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.test.Test
import kotlin.test.assertEquals

class AsmJsonTest {
    @Test
    fun testPPClass() {
        val ppClassPath = Paths.get("src/test/resources/pp.class")
        println("ppClassPath.exists() = ${ppClassPath.exists()}")
        val tmpPath = Paths.get("build/test")
        val ppJsonPath = tmpPath.resolve("pp.json")
        val ppNewClassPath = tmpPath.resolve("pp-new.class")
        val ppNewJsonPath = tmpPath.resolve("pp-new.json")

        val mavenClassLoaderHelper = MavenClassLoaderHelper()
        // 添加测试依赖
        mavenClassLoaderHelper.addDependencies(
            "org.apache.commons:commons-lang3:3.12.0",
            "javax.annotation:javax.annotation-api:1.3.2"
        )

        // 最好能把pp class所在的jar包添加到classloader中
//        mavenClassLoaderHelper.addDepFiles(Paths.get("ppinhar.jar").toFile())

        AsmJsonUtil.classFile2JsonFile(ppClassPath, ppJsonPath)
        AsmJsonUtil.jsonFile2ClassFile(ppJsonPath, ppNewClassPath, mavenClassLoaderHelper.getClassLoader())
        AsmJsonUtil.classFile2JsonFile(ppNewClassPath, ppNewJsonPath)

//        assertEquals(
//            ppJsonPath.readText(),
//            ppNewJsonPath.readText()
//        )
    }
}