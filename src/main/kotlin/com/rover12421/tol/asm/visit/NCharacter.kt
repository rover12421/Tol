package com.rover12421.tol.asm.visit

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.rover12421.tol.asm.visit.NAbs

class NCharacter @JsonCreator constructor(@JvmField @JsonProperty("value") var value: Char) : NAbs() {
    override fun toString(stringBuilder: StringBuilder) {
        stringBuilder
            .append("new Character((char)")
            .append(value.toInt())
            .append(')')
    }

}