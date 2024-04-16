package com.rover12421.tol.asm.visit

import com.rover12421.tol.asm.json.ASMifierJson
import com.rover12421.tol.asm.json.AsmJsonCompile

@Deprecated("")
class VNestMemberExperimental : VisitPrinter {
    @JvmField var nestMember: String? = null

    @Deprecated("")
    constructor() {
    }

    constructor(asMifier: ASMifierJson?, nestMember: String?) : super(asMifier) {
        this.nestMember = nestMember
    }

    override fun toString(stringBuilder: StringBuilder) {
//        stringBuilder.append(name + ".visitNestMemberExperimental(");
//        appendConstant(stringBuilder, nestMember);
//        stringBuilder.append(");\n\n");
        RuntimeException("VNestMemberExperimental 没有实现")
    }

    override fun compile(compile: AsmJsonCompile) {
//        compile.getClassWriter(name)
//                .visitNestMemberExperimental(nestMember);
        RuntimeException("VNestMemberExperimental 没有实现")
    }
}