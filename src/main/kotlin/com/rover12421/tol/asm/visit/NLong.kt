package com.rover12421.asmjson.visit

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

class NLong @JsonCreator constructor(@JvmField @JsonProperty("value") var value: Long) : NAbs() {
    override fun toString(stringBuilder: StringBuilder) {
        stringBuilder.append("new Long(").append(value).append("L)")
    }

}