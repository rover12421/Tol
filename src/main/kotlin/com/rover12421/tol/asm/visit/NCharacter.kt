package com.rover12421.asmjson.visit

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

class NCharacter @JsonCreator constructor(@JvmField @JsonProperty("value") var value: Char) : NAbs() {
    override fun toString(stringBuilder: StringBuilder) {
        stringBuilder
            .append("new Character((char)")
            .append(value.toInt())
            .append(')')
    }

}