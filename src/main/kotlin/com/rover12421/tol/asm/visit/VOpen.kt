package com.rover12421.tol.asm.visit

import com.rover12421.tol.asm.json.ASMifierJson
import com.rover12421.tol.asm.json.AsmJsonCompile

class VOpen : VisitPrinter {
    @JvmField var packaze: String? = null
    @JvmField var access = 0
    @JvmField var modules: Array<String>? = null

    @Deprecated("")
    constructor() {
    }

    constructor(
        asMifier: ASMifierJson?,
        packaze: String?,
        access: Int,
        modules: Array<String>
    ) : super(asMifier) {
        this.packaze = packaze
        this.access = access
        this.modules = modules
    }

    override fun toString(stringBuilder: StringBuilder) {
        stringBuilder.append("$name.visitOpen(")
        appendConstant(stringBuilder, packaze)
        stringBuilder.append(", ")
        appendAccessFlags(stringBuilder, access or ACCESS_MODULE)
        if (modules != null && modules!!.isNotEmpty()) {
            stringBuilder.append(", new String[] {")
            for (i in modules!!.indices) {
                stringBuilder.append(if (i == 0) " " else ", ")
                appendConstant(stringBuilder, modules!![i])
            }
            stringBuilder.append(" }")
        }
        stringBuilder.append(");\n")
    }

    override fun compile(compile: AsmJsonCompile) {
        compile.getModuleVisitor(name!!)!!
            .visitOpen(packaze, access, *modules?: emptyArray())
    }
}