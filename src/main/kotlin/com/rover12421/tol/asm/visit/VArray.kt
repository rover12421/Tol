package com.rover12421.tol.asm.visit

import com.rover12421.tol.asm.json.ASMifierJson
import com.rover12421.tol.asm.json.AsmJsonCompile

class VArray : VisitPrinter {
    @JvmField var arrayName: String? = null

    @Deprecated("")
    constructor() {
    }

    constructor(asmifier: ASMifierJson, arrayName: String?) : super(asmifier) {
        this.arrayName = arrayName
        nodes = asmifier.text
    }

    override fun toString(stringBuilder: StringBuilder) {
        stringBuilder.append("{\n")
        stringBuilder
            .append("AnnotationVisitor $name")
            .append(id)
            .append(" = $name")
        stringBuilder.append(id - 1).append(".visitArray(")
        appendConstant(stringBuilder, arrayName)
        stringBuilder.append(");\n")
        appendThisText(stringBuilder)
        appendVisitEnd(stringBuilder)
        stringBuilder.append("}\n")
    }

    private fun appendVisitEnd(stringBuilder: StringBuilder) {
        stringBuilder.append(name).append(id).append(VISIT_END)
    }

    override fun compile(compile: AsmJsonCompile) {
        val annotationVisitor = compile.getAnnotationVisitor(name + (id - 1))
            .visitArray(arrayName)
        compile.put(name + id, annotationVisitor)
        compileThis(compile)
        annotationVisitor.visitEnd()
    }
}