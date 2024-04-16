package com.rover12421.tol.asm.util

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.json.JsonWriteFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.rover12421.asmjson.ASMifierJson
import com.rover12421.asmjson.visit.VClass
import com.rover12421.lotus.util.FilesUtil
import com.rover12421.lotus.util.Log
import org.objectweb.asm.ClassReader
import org.objectweb.asm.util.TraceClassVisitor
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream
import kotlin.io.path.isDirectory

object AsmJsonUtil {
    @JvmStatic
    val objectMapper = ObjectMapper()
        .disable(MapperFeature.AUTO_DETECT_SETTERS)
        .disable(MapperFeature.AUTO_DETECT_GETTERS)
        .disable(MapperFeature.AUTO_DETECT_IS_GETTERS)
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)

    @JvmStatic
    val jsonWrite = objectMapper.writerWithDefaultPrettyPrinter().withFeatures(JsonWriteFeature.ESCAPE_NON_ASCII)

    @JvmStatic
    fun toJson(obj: Any): String {
        return jsonWrite.writeValueAsString(obj)
    }

    @JvmStatic
    fun <T> parseJson(json: String, type: Class<T>): T {
        return objectMapper.readValue(json, type)
    }

    @JvmStatic
    fun <T> parseJson(file: File, type: Class<T>): T {
        return objectMapper.readValue(file, type)
    }

    @JvmStatic
    fun <T> parseJson(file: Path, type: Class<T>): T {
        return objectMapper.readValue(file.toFile(), type)
    }

    @JvmStatic
    fun classFile2AsmJavaFile(clazzFile: Path, javaFile: Path) {
        Files.newInputStream(clazzFile).use { ins ->
            val asMifierJson = ASMifierJson()
            ClassReader(ins).accept(TraceClassVisitor(null, asMifierJson, null), 0)
            val javaCode = asMifierJson.vClass.toString()
            Files.createDirectories(javaFile.parent)
            Files.write(javaFile, javaCode.toByteArray(StandardCharsets.UTF_8))
        }
    }

    @JvmStatic
    fun class2VClass(classByteArray: ByteArray, asMifierJson: ASMifierJson = ASMifierJson()): VClass {
        ClassReader(classByteArray).accept(TraceClassVisitor(null, asMifierJson, null), 0)
        return asMifierJson.vClass
    }

    @JvmStatic
    fun class2VClass(classInputStream: InputStream, asMifierJson: ASMifierJson = ASMifierJson()): VClass {
        ClassReader(classInputStream).accept(TraceClassVisitor(null, asMifierJson, null), 0)
        return asMifierJson.vClass
    }

    @JvmStatic
    fun jar2VClassList(jarPath: String): MutableList<VClass> {
        val vClasses = mutableListOf<VClass>()
        ZipFile(jarPath).use { zip ->
            for (entry in zip.entries()) {
                if (entry.name.endsWith(".class")) {
                    zip.getInputStream(entry).use { zis ->
                        vClasses.add(class2VClass(zis))
                    }
                }
            }
        }
        return vClasses
    }

    const val AsmJsonFileExt = "asmj"

    @JvmStatic
    fun jar2json(jarPath: String, jsonDir: String, skipNonClazz: Boolean = false) {
        Log.debug("[jar2json] jarPath : $jarPath")
        Log.debug("[jar2json] jsonDir : $jsonDir")
        Log.debug("[jar2json] skipNonClazz : $skipNonClazz")
        ZipFile(jarPath).use { zip ->
            for (entry in zip.entries()) {
                if (entry.isDirectory) continue

                if (entry.name.endsWith(".class")) {
                    Log.debug("[jar2json] class entry : ${entry.name}")
                    zip.getInputStream(entry).use { zis ->
                        val vClass = class2VClass(zis)
                        val jsonFile = File("$jsonDir/${entry.name.substring(0, entry.name.length - 5)}${AsmJsonFileExt}")
                        jsonFile.parentFile.mkdirs()
                        jsonFile.outputStream().use {
                            it.write(vClass.toJson().toByteArray(StandardCharsets.UTF_8))
                        }
                    }
                } else if (!skipNonClazz) {
                    Log.debug("[jar2json] other entry : ${entry.name}")
                    zip.getInputStream(entry).use { zis ->
                        val outFile = File("$jsonDir/${entry.name}")
                        outFile.parentFile.mkdirs()
                        outFile.outputStream().use {
                            zis.copyTo(it)
                        }
                    }
                }
            }
        }
    }

    fun jsonDir2Jar(jsonDir: Path, outJar: Path, classLoader: ClassLoader = Thread.currentThread().contextClassLoader) {
        Log.debug("[jsonDir2Jar] outJar : $outJar")
        Log.debug("[jsonDir2Jar] jsonDir : $jsonDir")

        FileOutputStream(outJar.toString()).use { fos ->
            ZipOutputStream(fos).use { zos ->
                FilesUtil.walkFileTree(jsonDir) { file ->
                    if (!file.isDirectory()) {
                        var entryName = jsonDir.relativize(file).toString()
                        if (entryName.endsWith(AsmJsonFileExt)) {
                            entryName = entryName.substringBeforeLast(".", "") + ".class"
                        }
                        val entry = ZipEntry(entryName)
                        Log.debug("[jsonDir2Jar] add entry: $entryName")
                        entry.method = ZipEntry.DEFLATED
                        zos.putNextEntry(entry)
                        if (entryName.endsWith(".class")) {
                            zos.write(jsonFile2ClassByte(file, classLoader))
                        } else {
                            Files.newInputStream(file).use { ist ->
                                ist.copyTo(zos)
                            }
                        }
                        zos.closeEntry()
                    }
                }
            }
        }
    }

    @JvmStatic
    fun jar2AsmJava(jarPath: String, asmDir: String, skipNonClazz: Boolean = false) {
        Log.debug("[jar2AsmJava] jarPath : $jarPath")
        Log.debug("[jar2AsmJava] jsonDir : $asmDir")
        Log.debug("[jar2AsmJava] skipNonClazz : $skipNonClazz")
        ZipFile(jarPath).use { zip ->
            for (entry in zip.entries()) {
                if (entry.isDirectory) continue

                if (entry.name.endsWith(".class")) {
                    Log.debug("[jar2AsmJava] class entry : ${entry.name}")
                    zip.getInputStream(entry).use { zis ->
                        val vClass = class2VClass(zis)
                        val javaFile = File("$asmDir/${entry.name.substring(0, entry.name.length - 5)}java")
                        javaFile.parentFile.mkdirs()
                        javaFile.outputStream().use {
                            it.write(vClass.toString().toByteArray(StandardCharsets.UTF_8))
                        }
                    }
                } else if (!skipNonClazz) {
                    Log.debug("[jar2AsmJava] other entry : ${entry.name}")
                    zip.getInputStream(entry).use { zis ->
                        val outFile = File("$asmDir/${entry.name}")
                        outFile.parentFile.mkdirs()
                        outFile.outputStream().use {
                            zis.copyTo(it)
                        }
                    }
                }
            }
        }
    }

    fun class2VClass(clazzFile: Path, asMifierJson: ASMifierJson = ASMifierJson()): VClass {
        Files.newInputStream(clazzFile).use { ins ->
            ClassReader(ins).accept(TraceClassVisitor(null, asMifierJson, null), 0)
        }
        return asMifierJson.vClass
    }

    fun class2Json(clazzFile: Path, asMifierJson: ASMifierJson = ASMifierJson()): String {
        val vclass = class2VClass(clazzFile, asMifierJson)
        return vclass.toJson()
    }

    fun classFile2JsonFile(clazzFile: Path, javaFile: Path) {
        val javaCode = class2Json(clazzFile)
        Files.createDirectories(javaFile.parent)
        Files.write(javaFile, javaCode.toByteArray(StandardCharsets.UTF_8))
    }

    fun VClass2ClassBytes(vClass: VClass, classLoader: ClassLoader = Thread.currentThread().contextClassLoader): ByteArray {
        return vClass.toJvmClassBytes(classLoader)
    }

    fun VClass2ClassFile(vClass: VClass, clazzFile: Path, classLoader: ClassLoader = Thread.currentThread().contextClassLoader) {
        Files.createDirectories(clazzFile.parent)
        Files.write(clazzFile, VClass2ClassBytes(vClass, classLoader))
    }

    fun json2VClass(json: String)  = parseJson(json, VClass::class.java)

    fun json2ClassPath(json: String, clazzFile: Path, classLoader: ClassLoader = Thread.currentThread().contextClassLoader) {
//        Log.info(json)
        val jsonClazz = json2VClass(json)
        try {
//            Log.info(jsonClazz.toString())
            VClass2ClassFile(jsonClazz, clazzFile, classLoader)
        } catch (e: Throwable) {
            throw RuntimeException(jsonClazz.fullName, e)
        }
    }

    fun jsonFile2ClassByte(jsonFile: Path, classLoader: ClassLoader = Thread.currentThread().contextClassLoader): ByteArray {
        val json = Files.readAllBytes(jsonFile).toString(StandardCharsets.UTF_8)
        val jsonClazz = json2VClass(json)
        try {
            return VClass2ClassBytes(jsonClazz, classLoader)
        } catch (e: Throwable) {
            throw RuntimeException(jsonClazz.fullName, e)
        }
    }

    fun jsonFile2ClassFile(jsonFile: Path, clazzFile: Path, classLoader: ClassLoader = Thread.currentThread().contextClassLoader) {
        val json = Files.readAllBytes(jsonFile).toString(StandardCharsets.UTF_8)
        json2ClassPath(json, clazzFile, classLoader)
    }

    fun readAllClass(dir: Path) : List<VClass> {
        val allVClass = mutableListOf<VClass>()
        FilesUtil.walkFileTree(dir) { file ->
            if (file.toString().endsWith(".class")) {
                allVClass.add(AsmJsonUtil.class2VClass(file))
            }
        }

        return allVClass
    }

    fun classDir2AsmJavaDir(classDir: String, asmDir: String, copyUnknownFile: Boolean = true) = classDir2AsmJavaDir(Paths.get(classDir), Paths.get(asmDir), copyUnknownFile)
    fun classDir2AsmJavaDir(classDir: Path, asmDir: Path, copyUnknownFile: Boolean = true) {
        FilesUtil.walkFileTree(classDir) { clazzPath ->
            if (clazzPath.toString().endsWith(".class")) {
                val asmJavaPath = asmDir.resolve(classDir.relativize(clazzPath.parent)).resolve("${clazzPath.fileName.toFile().nameWithoutExtension}.java")
                Log.info("asm $clazzPath to $asmJavaPath")
                classFile2AsmJavaFile(clazzPath, asmJavaPath)
            } else {
                if (copyUnknownFile) {
                    val cpPath = asmDir.resolve(classDir.relativize(clazzPath))
                    Log.info("copy $clazzPath to $cpPath")
                    Files.createDirectories(cpPath.parent)
                    Files.copy(clazzPath, cpPath, StandardCopyOption.REPLACE_EXISTING)
                } else {
                    Log.info("skip copy $clazzPath")
                }
            }
        }
    }

    fun classDir2JsonDir(classDir: String, jsonDir: String, copyUnknownFile: Boolean = true) = classDir2JsonDir(Paths.get(classDir), Paths.get(jsonDir), copyUnknownFile)
    fun classDir2JsonDir(classDir: Path, jsonDir: Path, copyUnknownFile: Boolean = true) {
        FilesUtil.walkFileTree(classDir) { clazzPath ->
            if (clazzPath.toString().endsWith(".class")) {
                val jsonPath = jsonDir.resolve(classDir.relativize(clazzPath.parent)).resolve("${clazzPath.fileName.toFile().nameWithoutExtension}.json")
                Log.info("jsoner $clazzPath to $jsonPath")
                classFile2JsonFile(clazzPath, jsonPath)
            } else {
                if (copyUnknownFile) {
                    val cpPath = jsonDir.resolve(classDir.relativize(clazzPath))
                    Log.info("copy $clazzPath to $cpPath")
                    Files.createDirectories(cpPath.parent)
                    Files.copy(clazzPath, cpPath, StandardCopyOption.REPLACE_EXISTING)
                } else {
                    Log.info("skip copy $clazzPath")
                }
            }
        }
    }

    fun jsonDir2ClassDir(jsonDir: String, classDir: String, copyUnknownFile: Boolean = true) = jsonDir2ClassDir(Paths.get(jsonDir), Paths.get(classDir), copyUnknownFile)
    fun jsonDir2ClassDir(jsonDir: Path, classDir: Path, copyUnknownFile: Boolean = true) {
        FilesUtil.walkFileTree(jsonDir) { jsonPath ->
            if (jsonPath.toString().endsWith(".json")) {
                val clazzPath = classDir.resolve(jsonDir.relativize(jsonPath.parent)).resolve("${jsonPath.fileName.toFile().nameWithoutExtension}.class")
                Log.info("classer $jsonPath to $clazzPath")
                jsonFile2ClassFile(jsonPath, clazzPath)
            } else {
                if (copyUnknownFile) {
                    val cpPath = classDir.resolve(jsonDir.relativize(jsonPath))
                    Log.info("copy $jsonPath to $cpPath")
                    Files.createDirectories(cpPath.parent)
                    Files.copy(jsonPath, cpPath, StandardCopyOption.REPLACE_EXISTING)
                } else {
                    Log.info("skip copy $jsonPath")
                }
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val clazzPath = Paths.get("/Users/rover12421/Downloads/jeb64-2.3.6.201710121640/bin/Launcher.class")
        val jsonPath = Paths.get("/Users/rover12421/Downloads/jeb64-2.3.6.201710121640/bin/Launcher.json")
//        class2AsmJsonJava(clazzPath, jsonPath)
        jsonFile2ClassFile(jsonPath, clazzPath)
    }
}