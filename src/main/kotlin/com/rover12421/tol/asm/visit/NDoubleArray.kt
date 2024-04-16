package com.rover12421.asmjson.visit

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

class NDoubleArray @JsonCreator constructor(@JvmField @JsonProperty("value") var value: DoubleArray) : NAbs() {
    override fun toString(stringBuilder: StringBuilder) {
        stringBuilder.append("new double[] {")
        for (i in value.indices) {
            stringBuilder.append(if (i == 0) "" else ",").append(value[i]).append('d')
        }
        stringBuilder.append('}')
    }
}