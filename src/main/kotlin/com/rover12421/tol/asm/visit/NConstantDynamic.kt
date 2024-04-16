package com.rover12421.asmjson.visit

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeInfo
import mirror.org.objectweb.asm.ConstantDynamicMirror.getBootstrapMethodArgumentsUnsafe
import org.objectweb.asm.ConstantDynamic
import org.objectweb.asm.Handle

class NConstantDynamic @JsonCreator constructor(
    @JvmField @JsonProperty("constantDynamicName") var constantDynamicName: String,
    @JvmField @JsonProperty("descriptor") var descriptor: String,
    @JsonProperty("bootstrapMethod") bootstrapMethod: Handle,
    @JsonProperty("bootstrapMethodArguments") bootstrapMethodArguments: Array<Any?>?
) : NAbs() {
    @JvmField
    var bootstrapMethod: NHandle

    @JvmField
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
    var bootstrapMethodArguments: Array<Any?>?

    constructor(constantDynamic: ConstantDynamic) : this(
        constantDynamic.name,
        constantDynamic.descriptor,
        constantDynamic.bootstrapMethod,
        getBootstrapMethodArgumentsUnsafe!!.invoke(constantDynamic)
    ) {
    }

    override fun toString(stringBuilder: StringBuilder) {
        stringBuilder.append("new ConstantDynamic(\"")
        stringBuilder.append(constantDynamicName).append("\", \"")
        stringBuilder.append(descriptor).append("\", ")
        appendConstant(stringBuilder, bootstrapMethod)
        stringBuilder.append(", new Object[] {")
        for (i in bootstrapMethodArguments!!.indices) {
            appendConstant(stringBuilder, bootstrapMethodArguments!![i])
            if (i != bootstrapMethodArguments!!.size - 1) {
                stringBuilder.append(", ")
            }
        }
        stringBuilder.append("})")
    }

    init {
        this.bootstrapMethod = NHandle(bootstrapMethod)
        this.bootstrapMethodArguments = toVType(bootstrapMethodArguments)
    }
}