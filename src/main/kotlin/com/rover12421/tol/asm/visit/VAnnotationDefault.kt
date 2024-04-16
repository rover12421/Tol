package com.rover12421.asmjson.visit

import com.rover12421.asmjson.ASMifierJson
import com.rover12421.asmjson.compile.AsmJsonCompile

class VAnnotationDefault : VisitPrinter {
    @JvmField var fromName: String? = null

    @Deprecated("")
    constructor() {
    }

    constructor(asMifierJson: ASMifierJson, fromName: String?) : super(asMifierJson) {
        this.fromName = fromName
        nodes = asMifierJson.text
    }

    override fun toString(stringBuilder: StringBuilder) {
        stringBuilder
            .append("{\n")
            .append(name + id)
            .append(" = ")
            .append(fromName)
            .append(".visitAnnotationDefault();\n")
        appendThisText(stringBuilder!!)
        appendVisitEnd(stringBuilder)
        stringBuilder.append("}\n")
    }

    private fun appendVisitEnd(stringBuilder: StringBuilder) {
        stringBuilder.append(name).append(id).append(VISIT_END)
    }

    override fun compile(compile: AsmJsonCompile) {
        val annotationVisitor =
            compile.getMethodVisitor(fromName!!)!!.visitAnnotationDefault()
        compile.put(name + id, annotationVisitor)
        compileThis(compile)
        annotationVisitor.visitEnd()
    }
}