package com.rover12421.tol.asm.visit

import com.rover12421.tol.asm.json.ASMifierJson
import com.rover12421.tol.asm.json.AsmJsonCompile
import org.objectweb.asm.TypePath

class VTypeAnnotation : VisitPrinter {
    @JvmField var method: String? = null
    @JvmField var typeRef = 0
    @JvmField var typePath: String? = null
    @JvmField var descriptor: String? = null
    @JvmField var visible = false
    @JvmField var fromName: String? = null

    @Deprecated("")
    constructor() {
    }

    constructor(
        fromName: String?,
        asMifier: ASMifierJson?,
        method: String?,
        typeRef: Int,
        typePath: TypePath?,
        descriptor: String?,
        visible: Boolean
    ) : super(asMifier, true) {
        this.fromName = fromName
        this.method = method
        this.typeRef = typeRef
        this.typePath = typePath?.toString()
        this.descriptor = descriptor
        this.visible = visible
    }

    override fun toString(stringBuilder: StringBuilder) {
        stringBuilder
            .append("{\n")
            .append(name + id)
            .append(" = ")
            .append(fromName)
            .append(".")
            .append(method)
            .append("(")
            .append(typeRef)
        if (typePath == null) {
            stringBuilder.append(", null, ")
        } else {
            stringBuilder.append(", TypePath.fromString(\"").append(typePath).append("\"), ")
        }
        appendConstant(stringBuilder, descriptor)
        stringBuilder.append(", ").append(visible).append(");\n")
        appendThisText(stringBuilder)
        stringBuilder.append("}\n")
    }

    override fun compile(compile: AsmJsonCompile) {
        /**
         * toString 用的是 method 而不是 visitTypeAnnotation
         * 这个可能会有问题，遇到了再处理
         */
        val annotationVisitor = compile.getMethodVisitor(name!!)
            .visitTypeAnnotation(
                typeRef,
                TypePath.fromString(typePath),
                descriptor, visible
            )
        compile.put(name + id, annotationVisitor)
    }
}