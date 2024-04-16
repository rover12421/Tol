package com.rover12421.asmjson.visit

import com.rover12421.asmjson.ASMifierJson
import com.rover12421.asmjson.compile.AsmJsonCompile

class VPermittedSubclass : VisitPrinter {
    @JvmField var permittedSubclass: String? = null

    @Deprecated("")
    constructor() {
    }

    constructor(asMifier: ASMifierJson?, permittedSubclass: String?) : super(asMifier) {
        this.permittedSubclass = permittedSubclass
    }

    override fun toString(stringBuilder: StringBuilder) {
        stringBuilder.append("$name.visitPermittedSubclass(")
        appendConstant(stringBuilder, permittedSubclass)
        stringBuilder.append(END_PARAMETERS)
    }

    override fun compile(compile: AsmJsonCompile) {
        compile.getClassWriter(name!!).visitPermittedSubclass(permittedSubclass)
    }
}