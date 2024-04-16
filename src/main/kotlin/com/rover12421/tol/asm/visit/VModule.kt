package com.rover12421.tol.asm.visit

import com.rover12421.tol.asm.json.ASMifierJson
import com.rover12421.tol.asm.json.AsmJsonCompile

class VModule : VisitPrinter {
    @JvmField var moduleName: String? = null
    @JvmField var flags = 0
    @JvmField var version: String? = null
    @JvmField var classWriter: String? = null

    @Deprecated("")
    constructor() {
    }

    constructor(
        classWriter: String?,
        asMifier: ASMifierJson?,
        moduleName: String?,
        flags: Int,
        version: String?
    ) : super(asMifier, true) {
        this.classWriter = classWriter
        this.moduleName = moduleName
        this.flags = flags
        this.version = version
    }

    override fun toString(stringBuilder: StringBuilder) {
        stringBuilder.append("ModuleVisitor $name = $classWriter.visitModule(")
        appendConstant(stringBuilder, moduleName)
        stringBuilder.append(", ")
        appendAccessFlags(stringBuilder, flags or ACCESS_MODULE)
        stringBuilder.append(", ")
        appendConstant(stringBuilder, version)
        stringBuilder.append(END_PARAMETERS)
        appendThisText(stringBuilder)
        appendVisitEnd(stringBuilder)
        stringBuilder.append("}\n")
    }

    private fun appendVisitEnd(stringBuilder: StringBuilder) {
        stringBuilder.append("$name.visitEnd();\n")
    }

    override fun compile(compile: AsmJsonCompile) {
        val classWriter = compile.getClassWriter(classWriter!!)
        val moduleVisitor = classWriter!!.visitModule(moduleName, flags, version)
        compile.put(name, moduleVisitor)
        moduleVisitor.visitEnd()
    }
}