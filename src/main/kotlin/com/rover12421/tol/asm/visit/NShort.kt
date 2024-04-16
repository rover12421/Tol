package com.rover12421.asmjson.visit

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

class NShort @JsonCreator constructor(@JvmField @JsonProperty("value") var value: Short) : NAbs() {
    override fun toString(stringBuilder: StringBuilder) {
        stringBuilder.append("new Short((short)").append(value.toInt()).append(')')
    }

}