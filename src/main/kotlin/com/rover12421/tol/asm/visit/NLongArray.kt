package com.rover12421.tol.asm.visit

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.rover12421.tol.asm.visit.NAbs

class NLongArray @JsonCreator constructor(@JvmField @JsonProperty("value") var value: LongArray) : NAbs() {
    override fun toString(stringBuilder: StringBuilder) {
        stringBuilder.append("new long[] {")
        for (i in value.indices) {
            stringBuilder.append(if (i == 0) "" else ",").append(value[i]).append('L')
        }
        stringBuilder.append('}')
    }

}