package com.rover12421.asmjson.visit

import com.rover12421.asmjson.ASMifierJson
import com.rover12421.asmjson.compile.AsmJsonCompile

class VMaxs : VisitPrinter {
    @JvmField var maxStack = 0
    @JvmField var maxLocals = 0

    @Deprecated("")
    constructor() {
    }

    constructor(asMifier: ASMifierJson?, maxStack: Int, maxLocals: Int) : super(asMifier) {
        this.maxStack = maxStack
        this.maxLocals = maxLocals
    }

    override fun toString(stringBuilder: StringBuilder) {
        stringBuilder
            .append(name)
            .append(".visitMaxs(")
            .append(maxStack)
            .append(", ")
            .append(maxLocals)
            .append(");\n")
    }

    override fun compile(compile: AsmJsonCompile) {
        compile.getMethodVisitor(name!!)
            .visitMaxs(maxStack, maxLocals)
    }

    companion object {
        fun getInstance(vMethod: VMethod, maxStack: Int, maxLocals: Int): VMaxs {
            val vMaxs = VMaxs(null, maxStack, maxLocals)
            vMaxs.name = vMethod.name
            return vMaxs
        }

        fun getInstance(vMethod: VMethod): VMaxs {
            return getInstance(vMethod, 0, 0)
        }
    }
}