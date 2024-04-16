package com.rover12421.asmjson.visit

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

class NIntArray @JsonCreator constructor(@JvmField @JsonProperty("value") var value: IntArray) : NAbs() {
    override fun toString(stringBuilder: StringBuilder) {
        stringBuilder.append("new int[] {")
        for (i in value.indices) {
            stringBuilder.append(if (i == 0) "" else ",").append(value[i])
        }
        stringBuilder.append('}')
    }

}