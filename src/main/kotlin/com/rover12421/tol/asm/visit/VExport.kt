package com.rover12421.tol.asm.visit

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.rover12421.tol.asm.json.ASMifierJson
import com.rover12421.tol.asm.json.AsmJsonCompile

class VExport(
    asMifier: ASMifierJson?,
    @JvmField var packaze: String?,
    @JvmField var access: Int,
    @JvmField var modules: Array<String>?
) : VisitPrinter(asMifier) {

    @JsonCreator
    constructor(
        @JsonProperty("packaze") packaze: String?,
        @JsonProperty("access") access: Int,
        @JsonProperty("modules") modules: Array<String>?
    ) : this(null, packaze, access, modules) {
    }

    override fun toString(stringBuilder: StringBuilder) {
        stringBuilder.append("$name.visitExport(")
        appendConstant(stringBuilder, packaze)
        stringBuilder.append(", ")
        appendAccessFlags(stringBuilder, access or ACCESS_MODULE)
        if (modules != null && modules!!.size > 0) {
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
        compile.getModuleVisitor(name!!)!!.visitExport(packaze, access, *modules?: emptyArray())
    }

}