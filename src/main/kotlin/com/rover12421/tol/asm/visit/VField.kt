package com.rover12421.tol.asm.visit

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.rover12421.tol.asm.json.ASMifierJson
import com.rover12421.tol.asm.json.AsmJsonCompile

class VField(
    @JvmField var classWriter: String,
    asMifier: ASMifierJson?,
    @JvmField var access: Int,
    @JvmField var fieldName: String,
    @JvmField var descriptor: String,
    @JvmField var signature: String?,
    value: Any?
) : VisitPrinter(asMifier, true) {

    @JvmField
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
    var value: Any? = toVType(asMifier, value)

    @JsonCreator
    constructor(
        @JsonProperty("classWriter") classWriter: String,
        @JsonProperty("access") access: Int,
        @JsonProperty("fieldName") fieldName: String,
        @JsonProperty("descriptor") descriptor: String,
        @JsonProperty("signature") signature: String?,
        @JsonProperty("value") value: Any?
    ) : this(classWriter, null, access, fieldName, descriptor, signature, value) {
    }

    override fun toString(stringBuilder: StringBuilder) {
        stringBuilder.append("{\n")
        stringBuilder.append("$name = $classWriter.visitField(")
        appendAccessFlags(stringBuilder, access or ACCESS_FIELD)
        stringBuilder.append(", ")
        appendConstant(stringBuilder, fieldName)
        stringBuilder.append(", ")
        appendConstant(stringBuilder, descriptor)
        stringBuilder.append(", ")
        appendConstant(stringBuilder, signature)
        stringBuilder.append(", ")
        appendConstant(stringBuilder, value)
        stringBuilder.append(");\n")
        appendThisText(stringBuilder)
        appendVisitEnd(stringBuilder)
        stringBuilder.append("}\n")
    }

    private fun appendVisitEnd(stringBuilder: StringBuilder) {
        stringBuilder.append(name).append(VISIT_END)
    }

    override fun compile(compile: AsmJsonCompile) {
        val fieldVisitor = compile.getClassWriter(classWriter!!)!!
            .visitField(access, fieldName, descriptor, signature, toCType(compile, value))
        compile.put(name, fieldVisitor)
        compileThis(compile)
        fieldVisitor.visitEnd()
    }

    companion object {
        fun getInstance(
            vClass: VClass,
            access: Int,
            fielName: String,
            descriptor: String,
            signature: String?,
            value: Any?
        ): VField {
            val vField = VField(vClass.name!!, null, access, fielName, descriptor, signature, value)
            vField.name = FIELDVISITOR
            return vField
        }

        fun getInstance(
            vClass: VClass,
            access: Int, fielName: String, descriptor: String
        ): VField {
            return getInstance(vClass, access, fielName, descriptor, null, null)
        }
    }

}