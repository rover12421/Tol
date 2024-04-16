package com.rover12421.tol.asm.visit

import com.rover12421.tol.asm.json.ASMifierJson
import com.rover12421.tol.asm.json.AsmJsonCompile

class VMethodInsn : VisitPrinter {
    @JvmField var opcode: String = ""
    @JvmField var owner: String = ""
    @JvmField var methodName: String = ""
    @JvmField var descriptor: String = ""
    @JvmField var isInterface = false

    @Deprecated("")
    constructor() {
    }

    constructor(
        asMifier: ASMifierJson?,
        opcode: Int,
        owner: String,
        methodName: String,
        descriptor: String,
        isInterface: Boolean
    ) : super(asMifier) {
        this.opcode = toOpCodeStr(opcode)
        this.owner = owner
        this.methodName = methodName
        this.descriptor = descriptor
        this.isInterface = isInterface
    }

    override fun toString(stringBuilder: StringBuilder) {
        stringBuilder
            .append(name)
            .append(".visitMethodInsn(")
            .append(opcode)
            .append(", ")
        appendConstant(stringBuilder, owner)
        stringBuilder.append(", ")
        appendConstant(stringBuilder, methodName)
        stringBuilder.append(", ")
        appendConstant(stringBuilder, descriptor)
        stringBuilder.append(", ")
        stringBuilder.append(if (isInterface) "true" else "false")
        stringBuilder.append(");\n")
    }

    override fun compile(compile: AsmJsonCompile) {
        compile.getMethodVisitor(name!!)
            .visitMethodInsn(toOpCode(opcode!!), owner, methodName, descriptor, isInterface)
    }

    companion object {
        fun getInstance(
            vMethod: VMethod,
            opcode: Int,
            owner: String,
            methodName: String,
            descriptor: String,
            isInterface: Boolean
        ): VMethodInsn {
            val vMethodInsn = VMethodInsn(null, opcode, owner, methodName, descriptor, isInterface)
            vMethodInsn.name = vMethod.name
            return vMethodInsn
        }
    }
}