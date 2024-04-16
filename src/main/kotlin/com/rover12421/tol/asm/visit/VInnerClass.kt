package com.rover12421.tol.asm.visit

import com.rover12421.tol.asm.json.ASMifierJson
import com.rover12421.tol.asm.json.AsmJsonCompile

class VInnerClass : VisitPrinter {
    @JvmField var innerClassName: String? = null
    @JvmField var outerName: String? = null
    @JvmField var innerName: String? = null
    @JvmField var access = 0

    @Deprecated("")
    constructor() {
    }

    constructor(
        asMifier: ASMifierJson?,
        innerClassName: String?,
        outerName: String?,
        innerName: String?,
        access: Int
    ) : super(asMifier) {
        this.innerClassName = innerClassName
        this.outerName = outerName
        this.innerName = innerName
        this.access = access
    }

    override fun toString(stringBuilder: StringBuilder) {
        stringBuilder.append("$name.visitInnerClass(")
        appendConstant(stringBuilder, innerClassName)
        stringBuilder.append(", ")
        appendConstant(stringBuilder, outerName)
        stringBuilder.append(", ")
        appendConstant(stringBuilder, innerName)
        stringBuilder.append(", ")
        appendAccessFlags(stringBuilder, access or ACCESS_INNER)
        stringBuilder.append(END_PARAMETERS)
    }

    override fun compile(compile: AsmJsonCompile) {
        compile.getClassWriter(name!!)
            .visitInnerClass(innerClassName, outerName, innerName, access)
    }
}