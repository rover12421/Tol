package com.rover12421.tol.asm.json

import com.rover12421.tol.asm.visit.NLabel
import org.objectweb.asm.*
import java.util.*

class AsmJsonCompile(val classLoader: ClassLoader) {

    private val objs: MutableMap<String, Any> = LinkedHashMap()
    private val labels: MutableMap<String, Label> = LinkedHashMap()
    var cw: ClassWriter? = null

    fun getObject(name: String): Any? {
        return objs[name]
    }

    fun getClassWriter(name: String): ClassWriter {
        return objs[name] as ClassWriter
    }

    fun getFieldVisitor(name: String): FieldVisitor {
        return objs[name] as FieldVisitor
    }

    fun getMethodVisitor(name: String): MethodVisitor {
        return objs[name] as MethodVisitor
    }

    fun getAnnotationVisitor(name: String): AnnotationVisitor {
        return objs[name] as AnnotationVisitor
    }

    fun getModuleVisitor(name: String): ModuleVisitor {
        return objs[name] as ModuleVisitor
    }

    fun resetLables() {
        labels.clear()
    }

    fun getLabel(nLabel: NLabel?): Label? {
        if (nLabel == null) return null
        var label = labels[nLabel.labelName]
        if (label == null) {
            label = Label()
            labels[nLabel.labelName] = label
            return label
        }
        return label
    }

    fun put(name: String?, any: Any) {
        if (name == null) {
            throw RuntimeException("put key is null")
        }
        objs[name] = any
        if (cw == null && any is ClassWriter) {
            cw = any
        }
    }

    fun toByteArray(): ByteArray {
        return cw!!.toByteArray()
    }


    companion object {
        val EMPTY = AsmJsonCompile(Thread.currentThread().contextClassLoader)


        /**
         * 0 : 将不会自动进行计算。你必须自己计算帧、局部变量和操作数栈的大小
         * ClassWriter.COMPUTE_MAXS 局部变量和操作数栈的大小就会自动计算。但是，你仍然需要自己调用visitMaxs方法，尽管你可以使用任何参数：实际上这些参数会被忽略，然后重新计算。使用这个选项，你仍然需要计算帧的大小
         * ClassWriter.COMPUTE_FRAMES 所有的大小都将自动为你计算。你不需要调用visitFrame方法，但是你仍然需要调用visitMaxs方法（参数将被忽略然后重新计算）
         *
         * COMPUTE_MAXS选项将使得ClassWriter慢10%，使用COMPUTE_FRAMES选项将使得ClassWriter慢两倍
         */
        //    public int classWriterFlag = ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS;
        //    public int classWriterFlag = ClassWriter.COMPUTE_MAXS;
//        var classWriterFlag = 0
            var classWriterFlag = ClassWriter.COMPUTE_FRAMES or ClassWriter.COMPUTE_MAXS
    }

}