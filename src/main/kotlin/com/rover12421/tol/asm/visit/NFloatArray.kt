package com.rover12421.asmjson.visit

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

class NFloatArray @JsonCreator constructor(@JvmField @JsonProperty("value") var value: FloatArray) : NAbs() {
    override fun toString(stringBuilder: StringBuilder) {
        stringBuilder.append("new float[] {")
        for (i in value.indices) {
            stringBuilder.append(if (i == 0) "" else ",").append(value[i]).append('f')
        }
        stringBuilder.append('}')
    }

}