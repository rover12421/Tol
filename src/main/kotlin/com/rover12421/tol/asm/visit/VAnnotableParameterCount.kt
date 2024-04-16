package com.rover12421.asmjson.visit

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.rover12421.asmjson.ASMifierJson
import com.rover12421.asmjson.compile.AsmJsonCompile

class VAnnotableParameterCount(asMifier: ASMifierJson?,
                               @JvmField var parameterCount: Int,
                               @JvmField var visible: Boolean) : VisitPrinter(asMifier) {
    @JsonCreator
    constructor(
        @JsonProperty("parameterCount") parameterCount: Int = 0,
        @JsonProperty("visible") visible: Boolean = false
    ): this(null, parameterCount, visible)

    override fun toString(stringBuilder: StringBuilder) {
        stringBuilder
            .append(name)
            .append(".visitAnnotableParameterCount(")
            .append(parameterCount)
            .append(", ")
            .append(visible)
            .append(");\n")
    }

    override fun compile(compile: AsmJsonCompile) {
        compile.getMethodVisitor(name!!)
            .visitAnnotableParameterCount(parameterCount, visible)
    }
}