package com.rover12421.asmjson.visit

import com.rover12421.asmjson.ASMifierJson
import com.rover12421.asmjson.compile.AsmJsonCompile

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