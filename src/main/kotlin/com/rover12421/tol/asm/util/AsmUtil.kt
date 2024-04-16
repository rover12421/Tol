@file:Suppress("NOTHING_TO_INLINE")

package com.rover12421.tol.asm.util


import com.rover12421.tol.asm.compile.AsmDumpJavaCompile
import com.rover12421.tol.util.FilesUtil
import com.rover12421.tol.util.Log
import org.objectweb.asm.ClassReader
import org.objectweb.asm.util.ASMifier
import org.objectweb.asm.util.Printer
import org.objectweb.asm.util.TraceClassVisitor
import java.io.ByteArrayOutputStream
import java.io.PrintWriter
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.zip.ZipFile

inline fun String.toJvmStyle() = AsmUtil.toJvmClazz(this)
inline fun String.toJavaStyle() = AsmUtil.toJavaClazz(this)

/**
 * Created by rover12421 on 3/10/17.
 */
object AsmUtil {
    @JvmStatic
    fun classFile2AsmJavaFile(clazzFile: Path, javaFile: Path, printer: Printer = ASMifier()) {
        Files.newInputStream(clazzFile).use { ins ->
            ByteArrayOutputStream().use { bos ->
                PrintWriter(bos).use { pw ->
                    ClassReader(ins).accept(TraceClassVisitor(null, printer, pw), 0)
                    val javaCode = bos.toString()
                    Files.createDirectories(javaFile.parent)
                    Files.write(javaFile, javaCode.toByteArray(StandardCharsets.UTF_8))
                }
            }
        }
    }

    @JvmStatic
    fun classDir2AsmJavaDir(classDir: String, asmJavaDir: String, copyUnknownFile: Boolean = true) = classDir2AsmJavaDir(
        Paths.get(classDir),
        Paths.get(asmJavaDir),
        copyUnknownFile
    )
    @JvmStatic
    fun classDir2AsmJavaDir(
        classDir: Path,
        asmJavaDir: Path,
        copyUnknownFile: Boolean = true
    ) {
        FilesUtil.walkFileTree(classDir) { clazzPath ->
            if (clazzPath.toString().endsWith(".class")) {
                val javaPath = asmJavaDir.resolve(classDir.relativize(clazzPath.parent)).resolve("${clazzPath.fileName.toFile().nameWithoutExtension}.java")
                Log.info("asmJava $clazzPath to $javaPath")
                classFile2AsmJavaFile(clazzPath, javaPath)
            } else {
                if (copyUnknownFile) {
                    val cpPath = asmJavaDir.resolve(classDir.relativize(clazzPath))
                    Log.info("copy $clazzPath to $cpPath")
                    Files.createDirectories(cpPath.parent)
                    Files.copy(clazzPath, cpPath, StandardCopyOption.REPLACE_EXISTING)
                } else {
                    Log.info("skip copy $clazzPath")
                }
            }
        }
    }

    @JvmStatic
    fun asmJava2ClassFile(
        asmJavaRoot: Path,
        clazzRoot: Path,
        packageName: String,
        clazzName: String
    ) {
        Files.createDirectories(clazzRoot)
        var packageName2Path = packageName.replace(".", "/")
        if (!packageName.isEmpty()) {
            packageName2Path += "/"
        }
        val javaFile = asmJavaRoot.resolve("$packageName2Path$clazzName.java")
        val clazzFile = clazzRoot.resolve("$packageName2Path$clazzName.class")

        val fullClazzName = "$packageName2Path$clazzName".replace("/", ".")

        Log.info("[asmJava2ClassFile] : $fullClazzName")
        AsmDumpJavaCompile.compile2class(javaFile, clazzFile)
    }

    /**
     * 转出Jvm类型
     */
    @JvmStatic
    fun toJvmType(type: String): String {
        var typeStr = type.toJvmStyle()
        val sb = StringBuilder()
        while (typeStr[0] == '[') {
            sb.append('[')
            typeStr = typeStr.substring(1)
        }

        when (typeStr) {
            "byte", "B" -> sb.append("B")
            "char", "C" -> sb.append("C")
            "double", "D" -> sb.append("D")
            "float", "F" -> sb.append("F")
            "int", "I" -> sb.append("I")
            "short", "S" -> sb.append("S")
            "void", "V" -> sb.append("V")
            "long", "J" -> sb.append("J")
            "boolean", "Z" -> sb.append("Z")
            else -> if (typeStr.startsWith("L") && typeStr.endsWith(";")) {
                sb.append(toJvmClazz(typeStr))
            } else {
                sb.append("L")
                sb.append(toJvmClazz(typeStr))
                sb.append(";")
            }
        }
        return sb.toString()
    }

    @JvmStatic
    fun toJvmMethodDesc(retType: Class<*>, vararg paramTypes: Class<*>): String {
        return toJvmMethodDesc(retType.name, *paramTypes.map { it.name }.toTypedArray())
    }

    /**
     * 通过返回类型,参数类型,转换成Jvm格式的方法描述
     */
    @JvmStatic
    fun toJvmMethodDesc(retType: String, vararg paramTypes: String): String {
        val sb = StringBuilder()
        sb.append("(")
        for (paramType in paramTypes) {
            sb.append(toJvmType(paramType))
        }

        sb.append(")")
        sb.append(toJvmType(retType))
        return sb.toString()
    }

    /**
     * 转换成 . 模式的Java类描述
     */
    @JvmStatic
    inline fun toJavaClazz(clazz: String): String {
        return clazz.replace("/", ".")
    }

    /**
     * 转换成 / 模式的Jvm类描述
     */
    @JvmStatic
    inline fun toJvmClazz(clazz: String): String {
        return clazz.replace(".", "/")
    }

    @JvmStatic
    fun getFullClassNameByJavaStyple(packageName: String?, clazzName: String): String {
        if (packageName == null || packageName.isBlank()) {
            return clazzName
        }

        return toJavaClazz("$packageName.$clazzName")
    }

    @JvmStatic
    fun getFullClassNameByJvmStyple(packageName: String?, clazzName: String): String {
        if (packageName == null || packageName.isBlank()) {
            return clazzName
        }

        return toJvmClazz("$packageName.$clazzName")
    }

    @JvmStatic
    fun asmJavaDir2classDir(asmJavaDir: Path, classDir: Path) {
        FilesUtil.walkFileTree(asmJavaDir) { file->
            if (file.toString().endsWith(".java")) {
                val relativizePath = asmJavaDir.relativize(file)
                val fileName = relativizePath.fileName.toString()
                val fullPath = relativizePath.toString()

                var packageNameSign = ""
                if (fullPath.contains("/")) {
                    packageNameSign = fullPath.substring(0, fullPath.length - fileName.length - 1)
                }
                val packageName = packageNameSign.replace("/", ".")

                val clazzName = fileName.substring(0, fileName.length-5)

                AsmUtil.asmJava2ClassFile(
                    asmJavaRoot =  asmJavaDir,
                    clazzRoot = classDir,
                    packageName = packageName,
                    clazzName = clazzName
                )

            }
        }
    }

    @JvmStatic
    fun getPackageName(fullClazz: String): String {
        val clazz = fullClazz.toJavaStyle()
        return if (clazz.contains(".")) {
            clazz.substring(0, clazz.lastIndexOf("."))
        } else {
            ""
        }
    }

    @JvmStatic
    fun getSimpleClassName(fullClazz: String): String {
        val clazz = fullClazz.toJavaStyle()
        return if (clazz.contains(".")) {
            clazz.substring(clazz.lastIndexOf(".")+1)
        } else {
            clazz
        }
    }

    @JvmStatic
    fun jar2asmJava(jarPath: Path, outPath: Path) {
        ZipFile(jarPath.toFile()).use { zipFile ->
            for (entry in zipFile.entries()) {

                if (entry.isDirectory) continue

                val name = entry.name
                if (name.endsWith(".class")) {
                    val jname = name.substring(0, name.length - 5) + "java"
                    Log.info("[jar2asmJava] jname : $jname")
                    val outFile = outPath.resolve(jname)
                    zipFile.getInputStream(entry).use { zis ->
                        ByteArrayOutputStream().use { bos ->
                            PrintWriter(bos).use { pw ->
                                ClassReader(zis).accept(TraceClassVisitor(null, ASMifier(), pw), 0)
                                val javaCode = bos.toString()
                                Files.createDirectories(outFile.parent)
                                Files.write(outFile, javaCode.toByteArray(StandardCharsets.UTF_8))
                            }
                        }
                    }
                }
            }
        }
    }
}