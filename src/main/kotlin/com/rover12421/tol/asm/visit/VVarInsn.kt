package com.rover12421.tol.asm.visit

import com.rover12421.tol.asm.json.ASMifierJson
import com.rover12421.tol.asm.json.AsmJsonCompile

class VVarInsn : VisitPrinter {
    @JvmField var opcode: String? = null
    @JvmField var `var` = 0

    @Deprecated("")
    constructor() {
    }

    constructor(asMifier: ASMifierJson?, opcode: Int, `var`: Int) : super(asMifier) {
        this.opcode = toOpCodeStr(opcode)
        this.`var` = `var`
    }

    override fun toString(stringBuilder: StringBuilder) {
        stringBuilder
            .append(name)
            .append(".visitVarInsn(")
            .append(opcode)
            .append(", ")
            .append(`var`)
            .append(");\n")
    }

    override fun compile(compile: AsmJsonCompile) {
        compile.getMethodVisitor(name!!)
            .visitVarInsn(toOpCode(opcode!!), `var`)
    }

    companion object {
        fun getInstance(vMethod: VMethod, opcode: Int, `var`: Int): VVarInsn {
            val vVarInsn = VVarInsn(null, opcode, `var`)
            vVarInsn.name = vMethod.name
            return vVarInsn
        }
    }
}