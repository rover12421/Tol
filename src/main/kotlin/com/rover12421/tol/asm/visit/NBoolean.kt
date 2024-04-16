package com.rover12421.tol.asm.visit

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.rover12421.tol.asm.visit.NAbs

class NBoolean @JsonCreator constructor(
    @JvmField @JsonProperty("value")var value: Boolean) : NAbs() {
    override fun toString(stringBuilder: StringBuilder) {
        stringBuilder.append(if (value) "Boolean.TRUE" else "Boolean.FALSE")
    }

}