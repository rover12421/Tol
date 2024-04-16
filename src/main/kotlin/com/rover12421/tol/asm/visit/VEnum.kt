package com.rover12421.asmjson.visit

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.rover12421.asmjson.ASMifierJson
import com.rover12421.asmjson.compile.AsmJsonCompile

class VEnum(
    asMifier: ASMifierJson?,
    @JvmField var enumName: String?,
    @JvmField var descriptor: String?,
    @JvmField var value: String?
) : VisitPrinter(asMifier) {

    @JsonCreator
    constructor(
        @JsonProperty("enumName") enumName: String?,
        @JsonProperty("descriptor") descriptor: String?,
        @JsonProperty("value") value: String?
    ) : this(null, enumName, descriptor, value) {
    }

    override fun toString(stringBuilder: StringBuilder) {
        stringBuilder.append(name).append(id).append(".visitEnum(")
        appendConstant(stringBuilder, enumName)
        stringBuilder.append(", ")
        appendConstant(stringBuilder, descriptor)
        stringBuilder.append(", ")
        appendConstant(stringBuilder, value)
        stringBuilder.append(");\n")
    }

    override fun compile(compile: AsmJsonCompile) {
        compile.getAnnotationVisitor(name + id)!!
            .visitEnum(enumName, descriptor, value)
    }

}