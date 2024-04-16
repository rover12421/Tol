package com.rover12421.asmjson.visit

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.rover12421.asmjson.ASMifierJson
import com.rover12421.asmjson.compile.AsmJsonCompile

class VVisit : VisitPrinter {
    @JvmField var vistName: String? = null

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
    var value: Any? = null

    @Deprecated("")
    constructor() {
    }

    constructor(asMifier: ASMifierJson?, vistName: String?, value: Any?) : super(asMifier) {
        this.vistName = vistName
        this.value = toVType(asMifier, value)
    }

    override fun toString(stringBuilder: StringBuilder) {
        stringBuilder.append(name).append(id).append(".visit(")
        appendConstant(stringBuilder, vistName)
        stringBuilder.append(", ")
        appendConstant(stringBuilder, value)
        stringBuilder.append(");\n")
    }

    override fun compile(compile: AsmJsonCompile) {
        compile.getAnnotationVisitor(name + id)
            .visit(vistName, toCType(compile, value))
    }
}