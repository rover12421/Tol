package com.rover12421.tol.asm.visit

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.rover12421.tol.asm.visit.NAbs
import org.objectweb.asm.Attribute
import org.objectweb.asm.util.ASMifierSupport

class NAttribute @JsonCreator constructor(
    @JsonProperty("type") @JvmField var type: String,
    @JsonProperty("isASMifierSupport") @JvmField var isASMifierSupport: Boolean

) : NAbs() {

    constructor(attribute: Attribute): this(attribute.type, attribute is ASMifierSupport)

    override fun toString(stringBuilder: StringBuilder) {}
}