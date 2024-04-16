package com.rover12421.tol.util

/**
 * Created by rover12421 on 11/29/19.
 */

import java.net.URL
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes

/**
 * Created by rover12421 on 3/10/17.
 */
object FilesUtil {
    /**
     * 遍历目录
     */
    fun walkFileTree(path: Path, block: (visitFile: Path)->Unit) {
        Files.walkFileTree(path, object : SimpleFileVisitor<Path>(){
            override fun visitFile(file: Path, attrs: BasicFileAttributes?): FileVisitResult {
                block(file)
                return FileVisitResult.CONTINUE
            }
        })
    }

    /**
     * 遍历所有指定扩展名的文件
     */
    fun getAllExtFile(path: String, ext: String) = getAllExtFile(Paths.get(path), ext)
    fun getAllExtFile(path: Path, ext: String): List<String> {
        return getAllExtFilePath(path, ext).map { it.toRealPath().toString() }
    }

    fun getAllExtFilePath(path: String, ext: String) = getAllExtFilePath(Paths.get(path), ext)
    fun getAllExtFilePath(path: Path, ext: String): List<Path> {
        val files = arrayListOf<Path>()
        walkFileTree(path) { file ->
            val fileStr = file.toRealPath().toString()
            if (fileStr.endsWith(ext)) {
                files.add(file)
            }
        }
        return files
    }

    fun getAllExtFileURL(path: String, ext: String) = getAllExtFileURL(Paths.get(path), ext)
    fun getAllExtFileURL(path: Path, ext: String): List<URL> {
        return getAllExtFilePath(path, ext).map { it.toUri().toURL() }
    }

    /**
     * 扫描解压目录里的所有class文件进行处理
     * block 是实际处理的代码
     */
    fun scanClassDir(
        scanDir: Path,
        block: (
            clazzFile: Path,    // $scanDir/a/b/c.class
            packageName: String, //a.b
            clazzName: String, //c
            fullClassName: String, //a.b.c
            javaClassFile : String // 带java后缀的java文件,相对路径 a/b/c.java
        ) -> Unit
    ) {
        FilesUtil.walkFileTree(scanDir) { file ->
            if (file.toString().endsWith(".class")) {
//                if (file.toString().endsWith("org/bytedeco/javacpp/annotation/StdString.class")) {
                val relativizePath = scanDir.relativize(file)
                val fileName = relativizePath.fileName.toString()
                val fullPath = relativizePath.toString()

                var packageNameSign = ""
                if (fullPath.contains("/")) {
                    packageNameSign = fullPath.substring(0, fullPath.length - fileName.length - 1)
                }
                val packageName = packageNameSign.replace("/", ".")

                val clazzName = fileName.substring(0, fileName.length-6)
                //Log.info("packageName : $packageName clazzName : $clazzName")

                val javaClassFile = when(packageNameSign) {
                    "" -> "$clazzName.java"
                    else -> "$packageNameSign/$clazzName.java"
                }

                val fullClassName = when(packageName) {
                    "" -> clazzName
                    else -> "$packageName.$clazzName"
                }

                block(file, packageName, clazzName, fullClassName, javaClassFile)

            }
        }
    }

}