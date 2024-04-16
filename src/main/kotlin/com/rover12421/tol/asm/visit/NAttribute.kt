package com.rover12421.asmjson.visit

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import org.objectweb.asm.Attribute
import org.objectweb.asm.util.ASMifierSupport

class NAttribute @JsonCreator constructor(
    @JsonProperty("type") @JvmField var type: String,
    @JsonProperty("isASMifierSupport") @JvmField var isASMifierSupport: Boolean

) : NAbs() {

    constructor(attribute: Attribute): this(attribute.type, attribute is ASMifierSupport)

    override fun toString(stringBuilder: StringBuilder) {}
}