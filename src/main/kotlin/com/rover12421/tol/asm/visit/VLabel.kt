package com.rover12421.tol.asm.visit

import com.rover12421.tol.asm.json.ASMifierJson
import com.rover12421.tol.asm.json.AsmJsonCompile
import org.objectweb.asm.Label

class VLabel : VisitPrinter {
    @JvmField var label: NLabel? = null

    @Deprecated("")
    constructor() {
    }

    constructor(asMifier: ASMifierJson?, label: Label?) : super(asMifier) {
        this.label = declareLabel(asMifier, label!!)
    }

    override fun toString(stringBuilder: StringBuilder) {
        stringBuilder.append(label)
        stringBuilder.append(name).append(".visitLabel(")
        appendLabel(stringBuilder, label!!)
        stringBuilder.append(");\n")
    }

    override fun compile(compile: AsmJsonCompile) {
        compile.getMethodVisitor(name!!)
            .visitLabel(compile.getLabel(label))
    }
}