package com.rover12421.tol.asm.visit

import com.rover12421.tol.asm.json.ASMifierJson
import com.rover12421.tol.asm.json.AsmJsonCompile

class VParameterAnnotation : VisitPrinter {
    @JvmField var parameter = 0
    @JvmField var descriptor: String? = null
    @JvmField var visible = false
    @JvmField var fromName: String? = null

    @Deprecated("")
    constructor() {
    }

    constructor(
        fromName: String?,
        asmifier: ASMifierJson,
        parameter: Int,
        descriptor: String?,
        visible: Boolean
    ) : super(asmifier) {
        this.fromName = fromName
        this.parameter = parameter
        this.descriptor = descriptor
        this.visible = visible
        nodes = asmifier.text
    }

    override fun toString(stringBuilder: StringBuilder) {
        stringBuilder
            .append("{\n")
            .append(name + id)
            .append(" = ")
            .append(fromName)
            .append(".visitParameterAnnotation(")
            .append(parameter)
            .append(", ")
        appendConstant(stringBuilder, descriptor)
        stringBuilder.append(", ").append(visible).append(");\n")
        appendThisText(stringBuilder)
        appendVisitEnd(stringBuilder)
        stringBuilder.append("}\n")
    }

    private fun appendVisitEnd(stringBuilder: StringBuilder) {
        stringBuilder.append(name).append(id).append(VISIT_END)
    }

    override fun compile(compile: AsmJsonCompile) {
        val annotationVisitor = compile.getMethodVisitor(fromName!!)
            .visitParameterAnnotation(parameter, descriptor, visible)
        compile.put(name + id, annotationVisitor)
        compileThis(compile)
        annotationVisitor.visitEnd()
    }
}