package com.rover12421.tol.asm.visit

import com.rover12421.tol.asm.json.ASMifierJson
import com.rover12421.tol.asm.json.AsmJsonCompile

class VSource : VisitPrinter {
    @JvmField var file: String? = null
    @JvmField var debug: String? = null

    @Deprecated("")
    constructor() {
    }

    constructor(asMifier: ASMifierJson?, file: String?, debug: String?) : super(asMifier) {
        this.file = file
        this.debug = debug
    }

    override fun toString(stringBuilder: StringBuilder) {
        stringBuilder.append("$name.visitSource(")
        appendConstant(stringBuilder, file)
        stringBuilder.append(", ")
        appendConstant(stringBuilder, debug)
        stringBuilder.append(END_PARAMETERS)
    }

    override fun compile(compile: AsmJsonCompile) {
        compile.getClassWriter(name!!).visitSource(file, debug)
    }
}