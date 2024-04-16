package com.rover12421.tol.asm.visit

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.rover12421.tol.asm.json.ASMifierJson
import com.rover12421.tol.asm.json.AsmJsonCompile

class VIincInsn(asMifier: ASMifierJson?,
                @JvmField var `var`: Int,
                @JvmField var increment: Int) : VisitPrinter(asMifier) {

    @JsonCreator
    constructor(
        @JsonProperty("var") `var`: Int,
        @JsonProperty("increment") increment: Int
    ) : this(null, `var`, increment) {
    }

    override fun toString(stringBuilder: StringBuilder) {
        stringBuilder
            .append(name)
            .append(".visitIincInsn(")
            .append(`var`)
            .append(", ")
            .append(increment)
            .append(");\n")
    }

    override fun compile(compile: AsmJsonCompile) {
        compile.getMethodVisitor(name!!)
            .visitIincInsn(`var`, increment)
    }

}