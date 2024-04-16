package com.rover12421.tol.asm.visit

import com.rover12421.tol.asm.json.ASMifierJson
import com.rover12421.tol.asm.json.AsmJsonCompile

class VPackage : VisitPrinter {
    @JvmField var packaze: String? = null

    @Deprecated("")
    constructor() {
    }

    constructor(asMifier: ASMifierJson?, packaze: String?) : super(asMifier) {
        this.packaze = packaze
    }

    override fun toString(stringBuilder: StringBuilder) {
        stringBuilder.append("$name.visitPackage(")
        appendConstant(stringBuilder, packaze)
        stringBuilder.append(");\n")
    }

    override fun compile(compile: AsmJsonCompile) {
        compile.getModuleVisitor(name!!)
            .visitPackage(packaze)
    }
}