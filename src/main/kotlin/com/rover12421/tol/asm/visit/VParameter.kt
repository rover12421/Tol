package com.rover12421.tol.asm.visit

import com.rover12421.tol.asm.json.ASMifierJson
import com.rover12421.tol.asm.json.AsmJsonCompile

class VParameter : VisitPrinter {
    @JvmField var parameterName: String? = null
    @JvmField var access = 0

    @Deprecated("")
    constructor() {
    }

    constructor(asMifier: ASMifierJson?, parameterName: String?, access: Int) : super(asMifier) {
        this.parameterName = parameterName
        this.access = access
    }

    override fun toString(stringBuilder: StringBuilder) {
        stringBuilder.append(name).append(".visitParameter(")
        appendString(stringBuilder, parameterName)
        stringBuilder.append(", ")
        appendAccessFlags(stringBuilder, access)
        stringBuilder.append(");\n")
    }

    override fun compile(compile: AsmJsonCompile) {
        compile.getMethodVisitor(name!!)
            .visitParameter(parameterName, access)
    }
}