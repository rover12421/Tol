package com.rover12421.asmjson.visit

import com.rover12421.asmjson.ASMifierJson
import com.rover12421.asmjson.compile.AsmJsonCompile
import org.objectweb.asm.Label

class VLineNumber : VisitPrinter {
    @JvmField var line = 0
    @JvmField var start: NLabel? = null

    @Deprecated("")
    constructor() {
    }

    constructor(asMifier: ASMifierJson?, line: Int, start: Label?) : super(asMifier) {
        this.line = line
        this.start = declareLabel(asMifier, start!!)
    }

    override fun toString(stringBuilder: StringBuilder) {
        stringBuilder.append(name).append(".visitLineNumber(").append(line).append(", ")
        appendLabel(stringBuilder, start!!)
        stringBuilder.append(");\n")
    }

    override fun compile(compile: AsmJsonCompile) {
        compile.getMethodVisitor(name!!)
            .visitLineNumber(line, NLabel2label(compile, start))
    }
}