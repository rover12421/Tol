package com.rover12421.asmjson.visit

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.rover12421.asm.RsClassWriter
import com.rover12421.asm.util.AsmUtil.getPackageName
import com.rover12421.asm.util.AsmUtil.getSimpleClassName
import com.rover12421.asmjson.ASMifierJson
import com.rover12421.asmjson.AsmJsonConstant
import com.rover12421.asmjson.compile.AsmJsonCompile
import com.rover12421.asmjson.util.AsmJsonUtil.toJson
import org.objectweb.asm.ClassWriter
import java.util.*

class VClass(
    asMifier: ASMifierJson?,
    @JvmField var version: Int,
    @JvmField var access: Int,
    @JvmField var fullName: String?,
    @JvmField var signature: String?,
    @JvmField var superName: String?,
    @JvmField var interfaces: Array<String>?
) : VisitPrinter(asMifier, true) {
    @JvmField var packageName = ""
    @JvmField var simpleName : String

    /**
     * 0 : 将不会自动进行计算。你必须自己计算帧、局部变量和操作数栈的大小
     * ClassWriter.COMPUTE_MAXS 局部变量和操作数栈的大小就会自动计算。但是，你仍然需要自己调用visitMaxs方法，尽管你可以使用任何参数：实际上这些参数会被忽略，然后重新计算。使用这个选项，你仍然需要计算帧的大小
     * ClassWriter.COMPUTE_FRAMES 所有的大小都将自动为你计算。你不需要调用visitFrame方法，但是你仍然需要调用visitMaxs方法（参数将被忽略然后重新计算）
     *
     * COMPUTE_MAXS选项将使得ClassWriter慢10%，使用COMPUTE_FRAMES选项将使得ClassWriter慢两倍
     */
    //    public int classWriterFlag = ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS;
    //    public int classWriterFlag = ClassWriter.COMPUTE_MAXS;
//    var classWriterFlag = ClassWriter.COMPUTE_FRAMES or ClassWriter.COMPUTE_MAXS
//    var classWriterFlag = 0
    var classWriterFlag = AsmJsonCompile.classWriterFlag

    @JsonCreator
    constructor(
        @JsonProperty("version") version: Int,
        @JsonProperty("access") access: Int,
        @JsonProperty("fullName") fullName: String,
        @JsonProperty("signature") signature: String?,
        @JsonProperty("superName") superName: String?,
        @JsonProperty("interfaces") interfaces: Array<String>?
    ) : this(null, version, access, fullName, signature, superName, interfaces)

    override fun toString(stringBuilder: StringBuilder) {
        if (!packageName.isEmpty()) {
            if (DEBUG) {
                stringBuilder.append("package asm.$packageName;\n")
            } else {
                stringBuilder.append("package $packageName;\n")
            }
        }
        stringBuilder.append("import org.objectweb.asm.AnnotationVisitor;\n")
        stringBuilder.append("import org.objectweb.asm.Attribute;\n")
        stringBuilder.append("import org.objectweb.asm.ClassReader;\n")
        stringBuilder.append("import org.objectweb.asm.ClassWriter;\n")
        stringBuilder.append("import org.objectweb.asm.ConstantDynamic;\n")
        stringBuilder.append("import org.objectweb.asm.FieldVisitor;\n")
        stringBuilder.append("import org.objectweb.asm.Handle;\n")
        stringBuilder.append("import org.objectweb.asm.Label;\n")
        stringBuilder.append("import org.objectweb.asm.MethodVisitor;\n")
        stringBuilder.append("import org.objectweb.asm.Opcodes;\n")
        stringBuilder.append("import org.objectweb.asm.Type;\n")
        stringBuilder.append("import org.objectweb.asm.TypePath;\n")
        if (DEBUG) {
            stringBuilder.append(
                """
    public class ${simpleName}Dump implements Opcodes {


    """.trimIndent()
            )
        } else {
            stringBuilder.append("public class $simpleName implements Opcodes {\n\n")
        }
        stringBuilder.append("public static byte[] $DUMPMETHODNAME () throws Exception {\n\n")
        stringBuilder.append("ClassWriter $name = new ClassWriter($classWriterFlag);\n")
        stringBuilder.append("FieldVisitor $FIELDVISITOR;\n")
        stringBuilder.append("MethodVisitor $METHODVISITOR;\n")
        stringBuilder.append("AnnotationVisitor $ANNOTATIONVISITOR0;\n\n")
        stringBuilder.append("$name.visit(")
        val versionString = CLASS_VERSIONS[version]
        if (versionString != null) {
            stringBuilder.append(versionString)
        } else {
            stringBuilder.append(version)
        }
        stringBuilder.append(", ")
        appendAccessFlags(stringBuilder, access or ACCESS_CLASS)
        stringBuilder.append(", ")
        appendConstant(stringBuilder, fullName)
        stringBuilder.append(", ")
        appendConstant(stringBuilder, signature)
        stringBuilder.append(", ")
        appendConstant(stringBuilder, superName)
        stringBuilder.append(", ")
        if (interfaces != null && interfaces!!.size > 0) {
            stringBuilder.append("new String[] {")
            for (i in interfaces!!.indices) {
                stringBuilder.append(if (i == 0) " " else ", ")
                appendConstant(stringBuilder, interfaces!![i])
            }
            stringBuilder.append(" }")
        } else {
            stringBuilder.append("null")
        }
        stringBuilder.append(END_PARAMETERS)
        appendThisText(stringBuilder)
        appendVisitEnd(stringBuilder)
    }

    private fun appendVisitEnd(stringBuilder: StringBuilder) {
        stringBuilder.append("$name.visitEnd();\n\n")
        stringBuilder.append("return $name.toByteArray();\n")
        stringBuilder.append("}\n")
        stringBuilder.append("}\n")
    }

    override fun compile(compile: AsmJsonCompile) {
        val classWriter = RsClassWriter(classWriterFlag, compile.classLoader)
        compile.put(name, classWriter)
        classWriter.visit(version, access, fullName, signature, superName, interfaces)
        try {
            compileThis(compile)
        } catch (e: Throwable) {
            println("[compile error]")
            throw e
        }
        classWriter.visitEnd()
    }

    @Transient
    private var methods: MutableList<VMethod>? = null
    fun getMethods(): List<VMethod> {
        if (methods != null) return methods!!
        val list: MutableList<VMethod> = ArrayList()
        for (node in nodes) {
            if (node is VMethod) {
                list.add(node)
            }
        }
        methods = list
        return methods!!
    }

    fun removeMethod(method: VMethod): Boolean {
        methods?.remove(method)
        return nodes.remove(method)
    }

    fun addMethod(method: VMethod) {
        methods!!.add(method)
        nodes.add(method)
    }

    @Transient
    private var fields: MutableList<VField>? = null
    fun getFields(): List<VField> {
        if (fields != null) return fields!!
        val list: MutableList<VField> = ArrayList()
        for (node in nodes) {
            if (node is VField) {
                list.add(node)
            }
        }
        fields = list
        return fields!!
    }

    fun removeField(field: VField): Boolean {
        fields!!.remove(field)
        return nodes.remove(field)
    }

    fun toJvmClassBytes(classLoader: ClassLoader?): ByteArray {
        val asmJsonCompile = AsmJsonCompile(classLoader!!)
        compile(asmJsonCompile)
        return asmJsonCompile.toByteArray()
    }

    fun toJson(): String {
        return toJson(this)
    }

    companion object {
        fun getInstance(
            access: Int, fullName: String,
            signature: String?,
            superName: String?,
            interfaces: Array<String>?
        ): VClass {
            val vClass =
                VClass(AsmJsonConstant.DefaultJvmVersion, access, fullName, signature, superName, interfaces)
            vClass.name = CLASSWRITER
            vClass.id = 0
            return vClass
        }

        fun getInstance(
            access: Int, fullName: String,
            superName: String?
        ): VClass {
            return getInstance(access, fullName, null, superName, null)
        }
    }

    init {
        if (fullName == null) {
            fullName = "module-info"
        }
        packageName = getPackageName(fullName!!)
        simpleName = getSimpleClassName(fullName!!)
    }
}