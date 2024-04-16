package com.rover12421.tol.asm.visit

import com.rover12421.tol.asm.json.ASMifierJson
import com.rover12421.tol.asm.json.AsmJsonCompile
import org.objectweb.asm.Label

class VTryCatchBlock : VisitPrinter {
    @JvmField var start: NLabel? = null
    @JvmField var end: NLabel? = null
    @JvmField var handler: NLabel? = null
    @JvmField var type: String? = null

    @Deprecated("")
    constructor() {
    }

    constructor(
        asMifier: ASMifierJson?,
        start: Label?,
        end: Label?,
        handler: Label?,
        type: String?
    ) : super(asMifier) {
        this.start = declareLabel(asMifier, start!!)
        this.end = declareLabel(asMifier, end!!)
        this.handler = declareLabel(asMifier, handler!!)
        this.type = type
    }

    override fun toString(stringBuilder: StringBuilder) {
        stringBuilder.append(start)
        stringBuilder.append(end)
        stringBuilder.append(handler)
        stringBuilder.append(name).append(".visitTryCatchBlock(")
        appendLabel(stringBuilder, start!!)
        stringBuilder.append(", ")
        appendLabel(stringBuilder, end!!)
        stringBuilder.append(", ")
        appendLabel(stringBuilder, handler!!)
        stringBuilder.append(", ")
        appendConstant(stringBuilder, type)
        stringBuilder.append(");\n")
    }

    override fun compile(compile: AsmJsonCompile) {
        compile.getMethodVisitor(name!!)
            .visitTryCatchBlock(
                NLabel2label(compile, start),
                NLabel2label(compile, end),
                NLabel2label(compile, handler),
                type
            )
    }
}