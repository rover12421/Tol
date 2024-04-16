package com.rover12421.tol.asm.visit

import com.rover12421.tol.asm.json.ASMifierJson
import com.rover12421.tol.asm.json.AsmJsonCompile

class VUse : VisitPrinter {
    @JvmField var service: String? = null

    @Deprecated("")
    constructor() {
    }

    constructor(asMifier: ASMifierJson?, service: String?) : super(asMifier) {
        this.service = service
    }

    override fun toString(stringBuilder: StringBuilder) {
        stringBuilder.append("$name.visitUse(")
        appendConstant(stringBuilder, service)
        stringBuilder.append(");\n")
    }

    override fun compile(compile: AsmJsonCompile) {
        compile.getModuleVisitor(name!!)
            .visitUse(service)
    }
}