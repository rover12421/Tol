package com.rover12421.asmjson.visit

import com.rover12421.asmjson.ASMifierJson
import com.rover12421.asmjson.compile.AsmJsonCompile

@Deprecated("")
class VNestHostExperimental : VisitPrinter {
    @JvmField var nestHost: String? = null

    @Deprecated("")
    constructor() {
    }

    constructor(asMifier: ASMifierJson?, nestHost: String?) : super(asMifier) {
        this.nestHost = nestHost
    }

    override fun toString(stringBuilder: StringBuilder) {
//        stringBuilder.append(name + ".visitNestHostExperimental(");
//        appendConstant(stringBuilder, nestHost);
//        stringBuilder.append(");\n\n");
        RuntimeException("VNestHostExperimental 没有实现")
    }

    override fun compile(compile: AsmJsonCompile) {
//        compile.getClassWriter(name)
//                .visitNestHostExperimental(nestHost);
        RuntimeException("VNestHostExperimental 没有实现")
    }
}