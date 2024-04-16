package com.rover12421.asmjson.visit

import com.rover12421.asmjson.ASMifierJson
import com.rover12421.asmjson.compile.AsmJsonCompile

class VProvide : VisitPrinter {
    @JvmField var service: String? = null
    @JvmField var providers: Array<String>? = null

    @Deprecated("")
    constructor() {
    }

    constructor(asMifier: ASMifierJson?, service: String?, providers: Array<String>) : super(asMifier) {
        this.service = service
        this.providers = providers
    }

    override fun toString(stringBuilder: StringBuilder) {
        stringBuilder.append("$name.visitProvide(")
        appendConstant(stringBuilder, service)
        stringBuilder.append(",  new String[] {")
        for (i in providers!!.indices) {
            stringBuilder.append(if (i == 0) " " else ", ")
            appendConstant(stringBuilder, providers!![i])
        }
        stringBuilder.append(END_ARRAY)
    }

    override fun compile(compile: AsmJsonCompile) {
        compile.getModuleVisitor(name!!)!!.visitProvide(service, *providers?: emptyArray())
    }
}