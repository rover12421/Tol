package com.rover12421.tol.asm.visit

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.rover12421.tol.asm.visit.NAbs

class NShort @JsonCreator constructor(@JvmField @JsonProperty("value") var value: Short) : NAbs() {
    override fun toString(stringBuilder: StringBuilder) {
        stringBuilder.append("new Short((short)").append(value.toInt()).append(')')
    }

}