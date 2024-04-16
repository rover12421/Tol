package com.rover12421.asmjson.visit

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

class NCharArray @JsonCreator constructor(@JvmField @JsonProperty("value") var value: CharArray) : NAbs() {
    override fun toString(stringBuilder: StringBuilder) {
        stringBuilder.append("new char[] {")
        for (i in value.indices) {
            stringBuilder.append(if (i == 0) "" else ",").append("(char)").append(value[i].toInt())
        }
        stringBuilder.append('}')
    }

}