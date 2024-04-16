package com.rover12421.tol.asm.visit

import com.rover12421.tol.asm.json.ASMifierJson
import com.rover12421.tol.asm.json.AsmJsonCompile

class VMainClass : VisitPrinter {
    @JvmField var mainClass: String? = null

    @Deprecated("")
    constructor() {
    }

    constructor(asMifier: ASMifierJson?, mainClass: String?) : super(asMifier) {
        this.mainClass = mainClass
    }

    override fun toString(stringBuilder: StringBuilder) {
        stringBuilder.append("$name.visitMainClass(")
        appendConstant(stringBuilder, mainClass)
        stringBuilder.append(");\n")
    }

    override fun compile(compile: AsmJsonCompile) {
        compile.getModuleVisitor(name!!)
            .visitMainClass(mainClass)
    }
}