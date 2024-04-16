package com.rover12421.asmjson.visit

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

class NInteger @JsonCreator constructor(@JvmField @JsonProperty("value") var value: Int) : NAbs() {
    override fun toString(stringBuilder: StringBuilder) {
        stringBuilder.append("new Integer(").append(value).append(')')
    }

}