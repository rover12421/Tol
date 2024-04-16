package com.rover12421.asmjson.visit

import com.rover12421.asmjson.ASMifierJson
import com.rover12421.asmjson.compile.AsmJsonCompile
import org.objectweb.asm.Label

class VJumpInsn : VisitPrinter {
    @JvmField var opcode: String? = null
    @JvmField var label: NLabel? = null

    @Deprecated("")
    constructor() {
    }

    constructor(asMifier: ASMifierJson?, opcode: Int, label: Label?) : super(asMifier) {
        this.opcode = toOpCodeStr(opcode)
        this.label = declareLabel(asMifier, label!!)
    }

    override fun toString(stringBuilder: StringBuilder) {
        stringBuilder.append(label)
        stringBuilder.append(name)
            .append(".visitJumpInsn(")
            .append(opcode).append(", ")
        appendLabel(stringBuilder, label!!)
        stringBuilder.append(");\n")
    }

    override fun compile(compile: AsmJsonCompile) {
        compile.getMethodVisitor(name!!)
            .visitJumpInsn(toOpCode(opcode!!), compile.getLabel(label))
    }
}