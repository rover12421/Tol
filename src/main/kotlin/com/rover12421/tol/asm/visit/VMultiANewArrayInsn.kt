package com.rover12421.tol.asm.visit

import com.rover12421.tol.asm.json.ASMifierJson
import com.rover12421.tol.asm.json.AsmJsonCompile

class VMultiANewArrayInsn : VisitPrinter {
    @JvmField var descriptor: String? = null
    @JvmField var numDimensions = 0

    @Deprecated("")
    constructor() {
    }

    constructor(asMifier: ASMifierJson?, descriptor: String?, numDimensions: Int) : super(asMifier) {
        this.descriptor = descriptor
        this.numDimensions = numDimensions
    }

    override fun toString(stringBuilder: StringBuilder) {
        stringBuilder.append(name).append(".visitMultiANewArrayInsn(")
        appendConstant(stringBuilder, descriptor)
        stringBuilder.append(", ").append(numDimensions).append(");\n")
    }

    override fun compile(compile: AsmJsonCompile) {
        compile.getMethodVisitor(name!!)
            .visitMultiANewArrayInsn(descriptor, numDimensions)
    }
}