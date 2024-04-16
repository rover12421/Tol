package com.rover12421.tol.asm.visit

import com.rover12421.tol.asm.json.ASMifierJson
import com.rover12421.tol.asm.json.AsmJsonCompile

class VInsn : VisitPrinter {
    @JvmField var opcode: String? = null

    @Deprecated("")
    constructor() {
    }

    constructor(asMifier: ASMifierJson?, opcode: Int) : super(asMifier) {
        this.opcode = toOpCodeStr(opcode)
    }

    override fun toString(stringBuilder: StringBuilder) {
        stringBuilder.append(name).append(".visitInsn(").append(opcode).append(");\n")
    }

    override fun compile(compile: AsmJsonCompile) {
        compile.getMethodVisitor(name!!)
            .visitInsn(toOpCode(opcode!!))
    }

    companion object {
        fun getInstance(vMethod: VMethod, opcode: Int): VInsn {
            val vInsn = VInsn(null, opcode)
            vInsn.name = vMethod.name
            return vInsn
        }
    }
}