package com.rover12421.asmjson.visit

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.rover12421.asmjson.ASMifierJson
import com.rover12421.asmjson.compile.AsmJsonCompile

class VLdcInsn : VisitPrinter {
    @JvmField
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
    var value: Any? = null

    @Deprecated("")
    constructor() {
    }

    constructor(asMifier: ASMifierJson?, value: Any?) : super(asMifier) {
        this.value = toVType(asMifier, value)
    }

    val cValue: Any?
        get() = toCType(AsmJsonCompile.EMPTY, value)

    fun setValue(value: Any?) {
        this.value = toVType(null, value)
    }

    override fun toString(stringBuilder: StringBuilder) {
        stringBuilder.append(name).append(".visitLdcInsn(")
        appendConstant(stringBuilder, value)
        stringBuilder.append(");\n")
    }

    override fun compile(compile: AsmJsonCompile) {
        compile.getMethodVisitor(name!!)
            .visitLdcInsn(toCType(compile, value))
    }
}