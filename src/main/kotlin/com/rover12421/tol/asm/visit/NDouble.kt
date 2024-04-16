package com.rover12421.asmjson.visit

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

class NDouble @JsonCreator constructor(@JvmField @JsonProperty("value") var value: Double) : NAbs() {
    override fun toString(stringBuilder: StringBuilder) {
        stringBuilder.append("new Double(\"").append(value).append("\")")
    }
}