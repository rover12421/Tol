package com.rover12421.asmjson.visit

import com.rover12421.asmjson.ASMifierJson
import com.rover12421.asmjson.compile.AsmJsonCompile

class VOuterClass : VisitPrinter {
    @JvmField var owner: String? = null
    @JvmField var outerClassName: String? = null
    @JvmField var descriptor: String? = null

    @Deprecated("")
    constructor() {
    }

    constructor(
        asMifier: ASMifierJson?,
        owner: String?,
        outerClassName: String?,
        descriptor: String?
    ) : super(asMifier) {
        this.owner = owner
        this.outerClassName = outerClassName
        this.descriptor = descriptor
    }

    override fun toString(stringBuilder: StringBuilder) {
        stringBuilder.append("$name.visitOuterClass(")
        appendConstant(stringBuilder, owner)
        stringBuilder.append(", ")
        appendConstant(stringBuilder, outerClassName)
        stringBuilder.append(", ")
        appendConstant(stringBuilder, descriptor)
        stringBuilder.append(END_PARAMETERS)
    }

    override fun compile(compile: AsmJsonCompile) {
        compile.getClassWriter(name!!)
            .visitOuterClass(owner, outerClassName, descriptor)
    }
}