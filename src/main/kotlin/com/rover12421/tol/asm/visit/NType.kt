package com.rover12421.asmjson.visit

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import org.objectweb.asm.Type

class NType @JsonCreator constructor(@JvmField @JsonProperty("descriptor") var descriptor: String) : NAbs() {

    constructor(type: Type) : this(type.descriptor) {}

    override fun toString(stringBuilder: StringBuilder) {
        stringBuilder.append("Type.getType(\"")
        stringBuilder.append(descriptor)
        stringBuilder.append("\")")
    }

}