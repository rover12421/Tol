package com.rover12421.tol.asm.visit

import com.rover12421.tol.asm.json.ASMifierJson
import com.rover12421.tol.asm.json.AsmJsonCompile
import org.objectweb.asm.Opcodes

class VIntInsn : VisitPrinter {
    @JvmField var opcode: String? = null
    @JvmField var operand = 0

    @Deprecated("")
    constructor() {
    }

    constructor(asMifier: ASMifierJson?, opcode: Int, operand: Int) : super(asMifier) {
        this.opcode = toOpCodeStr(opcode)
        this.operand = operand
    }

    override fun toString(stringBuilder: StringBuilder) {
        stringBuilder
            .append(name)
            .append(".visitIntInsn(")
            .append(opcode)
            .append(", ")
            .append(
                if (toOpCode(opcode!!) == Opcodes.NEWARRAY) operand else Integer.toString(
                    operand
                )
            )
            .append(");\n")
    }

    override fun compile(compile: AsmJsonCompile) {
        compile.getMethodVisitor(name!!)
            .visitIntInsn(toOpCode(opcode!!), operand)
    }
}