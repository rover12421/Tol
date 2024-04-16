package com.rover12421.tol.asm.visit

import com.rover12421.tol.asm.json.ASMifierJson
import com.rover12421.tol.asm.json.AsmJsonCompile

class VRequire : VisitPrinter {
    @JvmField var module: String? = null
    @JvmField var access: Int = 0
    @JvmField var version: String? = null

    @Deprecated("")
    constructor() {
    }

    constructor(
        asMifier: ASMifierJson?,
        module: String?,
        access: Int,
        version: String?
    ) : super(asMifier) {
        this.module = module
        this.access = access
        this.version = version
    }

    override fun toString(stringBuilder: StringBuilder) {
        stringBuilder.append("$name.visitRequire(")
        appendConstant(stringBuilder, module)
        stringBuilder.append(", ")
        appendAccessFlags(stringBuilder, access or ACCESS_MODULE)
        stringBuilder.append(", ")
        appendConstant(stringBuilder, version)
        stringBuilder.append(");\n")
    }

    override fun compile(compile: AsmJsonCompile) {
        compile.getModuleVisitor(name!!)
            .visitRequire(module, access, version)
    }
}