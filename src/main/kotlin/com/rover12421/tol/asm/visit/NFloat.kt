package com.rover12421.asmjson.visit

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

class NFloat @JsonCreator constructor(@JvmField @JsonProperty("value") var value: Float) : NAbs() {
    override fun toString(stringBuilder: StringBuilder) {
        stringBuilder.append("new Float(\"").append(value).append("\")")
    }

}