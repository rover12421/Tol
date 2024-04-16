package com.rover12421.asmjson.visit

import com.rover12421.asmjson.ASMifierJson
import com.rover12421.asmjson.compile.AsmJsonCompile
import org.objectweb.asm.Label

class VLocalVariable : VisitPrinter {
    @JvmField var varName: String? = null
    @JvmField var descriptor: String? = null
    @JvmField var signature: String? = null
    @JvmField var start: NLabel? = null
    @JvmField var end: NLabel? = null
    @JvmField var index = 0

    @Deprecated("")
    constructor() {
    }

    constructor(
        asMifier: ASMifierJson?,
        varName: String?,
        descriptor: String?,
        signature: String?,
        start: Label?,
        end: Label?,
        index: Int
    ) : super(asMifier) {
        this.varName = varName
        this.descriptor = descriptor
        this.signature = signature
        this.start = declareLabel(asMifier, start!!)
        this.end = declareLabel(asMifier, end!!)
        this.index = index
    }

    override fun toString(stringBuilder: StringBuilder) {
        stringBuilder.append(name).append(".visitLocalVariable(")
        appendConstant(stringBuilder, varName)
        stringBuilder.append(", ")
        appendConstant(stringBuilder, descriptor)
        stringBuilder.append(", ")
        appendConstant(stringBuilder, signature)
        stringBuilder.append(", ")
        appendLabel(stringBuilder, start!!)
        stringBuilder.append(", ")
        appendLabel(stringBuilder, end!!)
        stringBuilder.append(", ").append(index).append(");\n")
    }

    override fun compile(compile: AsmJsonCompile) {
        compile.getMethodVisitor(name!!)
            .visitLocalVariable(
                varName, descriptor, signature,
                NLabel2label(compile, start),
                NLabel2label(compile, end),
                index
            )
    }
}