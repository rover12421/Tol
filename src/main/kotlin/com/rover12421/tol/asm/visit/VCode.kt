package com.rover12421.asmjson.visit

import com.rover12421.asmjson.ASMifierJson
import com.rover12421.asmjson.compile.AsmJsonCompile

class VCode(asMifier: ASMifierJson?) : VisitPrinter(asMifier) {
    // 仅供反序列化使用
    @Deprecated("")
    constructor() : this(null) {
    }

    override fun toString(stringBuilder: StringBuilder) {
        stringBuilder.append("$name.visitCode();\n")
    }

    override fun compile(compile: AsmJsonCompile) {
        compile.getMethodVisitor(name!!).visitCode()
    }

    companion object {
        fun getInstance(vMethod: VMethod): VCode {
            val vCode = VCode(null)
            vCode.name = vMethod.name
            return vCode
        }
    }
}