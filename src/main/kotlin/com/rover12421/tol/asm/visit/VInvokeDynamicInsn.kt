package com.rover12421.asmjson.visit

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.rover12421.asmjson.ASMifierJson
import com.rover12421.asmjson.compile.AsmJsonCompile
import org.objectweb.asm.Handle

class VInvokeDynamicInsn : VisitPrinter {
    @JvmField  var invokeName: String = ""
    @JvmField  var descriptor: String = ""
    @JvmField  var bootstrapMethodHandle: NHandle? = null

    @JvmField
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
    var bootstrapMethodArguments: Array<Any?>? = null

    @Deprecated("")
    constructor() {
    }

    constructor(
        asMifier: ASMifierJson?,
        invokeName: String,
        descriptor: String,
        bootstrapMethodHandle: Handle,
        bootstrapMethodArguments: Array<Any?>?
    ) : super(asMifier) {
        this.invokeName = invokeName
        this.descriptor = descriptor
        this.bootstrapMethodHandle = NHandle(bootstrapMethodHandle)
        this.bootstrapMethodArguments = toVType(asMifier, bootstrapMethodArguments)
    }

    fun getBootstrapMethodArguments(): Array<Any?>? {
        return toCType(AsmJsonCompile.EMPTY, bootstrapMethodArguments)
    }

    override fun toString(stringBuilder: StringBuilder) {
        stringBuilder.append(name).append(".visitInvokeDynamicInsn(")
        appendConstant(stringBuilder, invokeName)
        stringBuilder.append(", ")
        appendConstant(stringBuilder, descriptor)
        stringBuilder.append(", ")
        appendConstant(stringBuilder, bootstrapMethodHandle)
        stringBuilder.append(", new Object[]{")
        for (i in bootstrapMethodArguments!!.indices) {
            appendConstant(stringBuilder, bootstrapMethodArguments!![i])
            if (i != bootstrapMethodArguments!!.size - 1) {
                stringBuilder.append(", ")
            }
        }
        stringBuilder.append("});\n")
    }

    override fun compile(compile: AsmJsonCompile) {
        compile.getMethodVisitor(name!!)
            .visitInvokeDynamicInsn(
                invokeName, descriptor,
                toCType(compile, bootstrapMethodHandle) as Handle?,
                *(toCType(compile, bootstrapMethodArguments)?: emptyArray())
            )
    }
}