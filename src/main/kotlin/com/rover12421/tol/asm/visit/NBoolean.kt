package com.rover12421.asmjson.visit

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

class NBoolean @JsonCreator constructor(
    @JvmField @JsonProperty("value")var value: Boolean) : NAbs() {
    override fun toString(stringBuilder: StringBuilder) {
        stringBuilder.append(if (value) "Boolean.TRUE" else "Boolean.FALSE")
    }

}