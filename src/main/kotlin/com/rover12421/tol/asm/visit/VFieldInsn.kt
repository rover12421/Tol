package com.rover12421.asmjson.visit

import com.rover12421.asmjson.ASMifierJson
import com.rover12421.asmjson.compile.AsmJsonCompile
import org.objectweb.asm.Opcodes

class VFieldInsn : VisitPrinter {
    @JvmField var opcode: String = ""
    @JvmField var owner: String = ""
    @JvmField var fieldName: String = ""
    @JvmField var descriptor: String = ""

    @Deprecated("")
    constructor() {
    }

    constructor(
        asMifier: ASMifierJson?,
        opcode: Int,
        owner: String,
        fieldName: String,
        descriptor: String
    ) : super(asMifier) {
        this.opcode = toOpCodeStr(opcode)
        this.owner = owner
        this.fieldName = fieldName
        this.descriptor = descriptor
    }

    override fun toString(stringBuilder: StringBuilder) {
        stringBuilder.append(name)
            .append(".visitFieldInsn(")
            .append(opcode)
            .append(", ")
        appendConstant(stringBuilder, owner)
        stringBuilder.append(", ")
        appendConstant(stringBuilder, fieldName)
        stringBuilder.append(", ")
        appendConstant(stringBuilder, descriptor)
        stringBuilder.append(");\n")
    }

    override fun compile(compile: AsmJsonCompile) {
        compile.getMethodVisitor(name!!)
            .visitFieldInsn(toOpCode(opcode!!), owner, fieldName, descriptor)
    }

    companion object {
        fun getInstance(
            vMethod: VMethod,
            opcode: Int,
            owner: String,
            fieldName: String,
            descriptor: String
        ): VFieldInsn {
            val vFieldInsn = VFieldInsn(null, opcode, owner, fieldName, descriptor)
            vFieldInsn.name = vMethod.name
            return vFieldInsn
        }

        fun getInstance(vMethod: VMethod, owner: String, vField: VField): VFieldInsn {
            var opcode = Opcodes.PUTFIELD
            if (vField.access and Opcodes.ACC_STATIC != 0) {
                opcode = Opcodes.PUTSTATIC
            }
            val vFieldInsn = VFieldInsn(null, opcode, owner, vField.fieldName, vField.descriptor)
            vFieldInsn.name = vMethod.name
            return vFieldInsn
        }
    }
}