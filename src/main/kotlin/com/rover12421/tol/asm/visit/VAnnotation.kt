package com.rover12421.tol.asm.visit

import com.rover12421.tol.asm.json.ASMifierJson
import com.rover12421.tol.asm.json.AsmJsonCompile
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.MethodVisitor

class VAnnotation : VisitPrinter {
    @JvmField var annotationName: String? = null
    @JvmField var descriptor: String? = null
    @JvmField var visible = false
    @JvmField var fromName: String? = null

    @Deprecated("")
    constructor() {
    }

    constructor(asMifier: ASMifierJson?, annotationName: String?, descriptor: String?) : super(
        asMifier,
        true
    ) {
        this.annotationName = annotationName
        this.descriptor = descriptor
    }

    constructor(
        fromName: String?,
        asMifier: ASMifierJson?,
        descriptor: String?,
        visible: Boolean
    ) : super(asMifier, true) {
        this.descriptor = descriptor
        this.visible = visible
        this.fromName = fromName
    }

    override fun toString(stringBuilder: StringBuilder) {
        if (fromName == null) {
            stringBuilder
                .append("{\n")
                .append("AnnotationVisitor $name")
                .append(id)
                .append(" = $name")
            stringBuilder.append(id - 1).append(".visitAnnotation(")
            appendConstant(stringBuilder, annotationName)
            stringBuilder.append(", ")
            appendConstant(stringBuilder, descriptor)
            stringBuilder.append(");\n")
        } else {
            stringBuilder
                .append("{\n")
                .append(name + id)
                .append(" = ")
                .append(fromName)
                .append(".visitAnnotation(")
            appendConstant(stringBuilder, descriptor)
            stringBuilder.append(", ").append(visible).append(");\n")
        }
        appendThisText(stringBuilder)
        appendVisitEnd(stringBuilder)
        stringBuilder.append("}\n")
    }

    private fun appendVisitEnd(stringBuilder: StringBuilder) {
        stringBuilder.append(name).append(id).append(VISIT_END)
    }

    override fun compile(compile: AsmJsonCompile) {
        val annotationVisitor: AnnotationVisitor
        if (fromName == null) {
            annotationVisitor = compile.getAnnotationVisitor(name + (id - 1))
                .visitAnnotation(annotationName, descriptor)
            compile.put(name + id, annotationVisitor)
        } else {
            val obj = compile.getObject(fromName!!)
            annotationVisitor = when (obj) {
                is ClassWriter -> {
                    obj.visitAnnotation(descriptor, visible)
                }
                is MethodVisitor -> {
                    obj.visitAnnotation(descriptor, visible)
                }
                is FieldVisitor -> {
                    obj.visitAnnotation(descriptor, visible)
                }
                else -> {
                    throw RuntimeException("待处理。。。")
                }
            }
            compile.put(name + id, annotationVisitor)
        }
        compileThis(compile)
        annotationVisitor.visitEnd()
    }
}