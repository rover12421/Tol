package com.rover12421.tol.asm.visit

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.rover12421.tol.asm.visit.NAbs

class NLabel @JsonCreator constructor(
    @JvmField @JsonProperty("labelName") var labelName: String,
    @JvmField @JsonProperty("first") var first: Boolean
) : NAbs() {
    override fun toString(stringBuilder: StringBuilder) {
        if (first) {
            stringBuilder.append("Label ").append(labelName).append(" = new Label();\n")
        }
    }

}