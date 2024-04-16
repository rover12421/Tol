package com.rover12421.asmjson.visit

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

class NByte @JsonCreator constructor(@JvmField @JsonProperty("value") var value: Byte) : NAbs() {
    override fun toString(stringBuilder: StringBuilder) {
        stringBuilder.append("new Byte((byte)").append(value.toInt()).append(')')
    }

}