package com.rover12421.tol.asm.visit

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.rover12421.tol.asm.visit.NAbs
import org.objectweb.asm.Type

class NType @JsonCreator constructor(@JvmField @JsonProperty("descriptor") var descriptor: String) : NAbs() {

    constructor(type: Type) : this(type.descriptor) {}

    override fun toString(stringBuilder: StringBuilder) {
        stringBuilder.append("Type.getType(\"")
        stringBuilder.append(descriptor)
        stringBuilder.append("\")")
    }

}