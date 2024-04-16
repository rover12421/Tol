package com.rover12421.tol.asm.visit

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.rover12421.tol.asm.visit.NAbs

class NInteger @JsonCreator constructor(@JvmField @JsonProperty("value") var value: Int) : NAbs() {
    override fun toString(stringBuilder: StringBuilder) {
        stringBuilder.append("new Integer(").append(value).append(')')
    }

}