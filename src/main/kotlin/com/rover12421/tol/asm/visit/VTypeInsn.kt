package com.rover12421.asmjson.visit

import com.rover12421.asmjson.ASMifierJson
import com.rover12421.asmjson.compile.AsmJsonCompile

class VTypeInsn : VisitPrinter {
    @JvmField var opcode: String? = null
    @JvmField var type: String? = null

    @Deprecated("")
    constructor() {
    }

    constructor(asMifier: ASMifierJson?, opcode: Int, type: String?) : super(asMifier) {
        this.opcode = toOpCodeStr(opcode)
        this.type = type
    }

    override fun toString(stringBuilder: StringBuilder) {
        stringBuilder.append(name).append(".visitTypeInsn(").append(opcode).append(", ")
        appendConstant(stringBuilder, type)
        stringBuilder.append(");\n")
    }

    override fun compile(compile: AsmJsonCompile) {
        compile.getMethodVisitor(name!!)
            .visitTypeInsn(toOpCode(opcode!!), type)
    }
}