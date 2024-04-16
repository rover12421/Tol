package com.rover12421.tol.asm.visit

import com.rover12421.tol.asm.json.ASMifierJson
import com.rover12421.tol.asm.json.AsmJsonCompile

class VNestMember : VisitPrinter {
    @JvmField var nestMember: String? = null

    @Deprecated("")
    constructor() {
    }

    constructor(asMifier: ASMifierJson?, nestMember: String?) : super(asMifier) {
        this.nestMember = nestMember
    }

    override fun toString(stringBuilder: StringBuilder) {
        stringBuilder.append("$name.visitNestMember(")
        appendConstant(stringBuilder, nestMember)
        stringBuilder.append(END_PARAMETERS)
    }

    override fun compile(compile: AsmJsonCompile) {
        compile.getClassWriter(name!!).visitNestMember(nestMember)
    }
}