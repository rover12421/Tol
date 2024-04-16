package com.rover12421.asmjson.visit

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.rover12421.asmjson.ASMifierJson
import com.rover12421.asmjson.compile.AsmJsonCompile
import org.objectweb.asm.Opcodes

class VFrame(
    asMifier: ASMifierJson?,
    @JvmField var type: Int,
    @JvmField var nLocal: Int,
    @JvmField @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS) var local: Array<Any>?,
    @JvmField var nStack: Int,
    @JvmField @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS) var stack: Array<Any>?
) : VisitPrinter(asMifier) {

    @JsonCreator
    constructor(
        @JsonProperty("type") type: Int,
        @JsonProperty("nLocal") nLocal: Int,
        @JsonProperty("local") local: Array<Any>?,
        @JsonProperty("nStack") nStack: Int,
        @JsonProperty("stack") stack: Array<Any>?
    ) : this(null, type, nLocal, local, nStack, stack)

    override fun toString(stringBuilder: StringBuilder) {
        when (type) {
            Opcodes.F_NEW, Opcodes.F_FULL -> {
                appendLables(stringBuilder, local)
                appendLables(stringBuilder, stack)
                if (type == Opcodes.F_NEW) {
                    stringBuilder.append(name).append(".visitFrame(Opcodes.F_NEW, ")
                } else {
                    stringBuilder.append(name).append(".visitFrame(Opcodes.F_FULL, ")
                }
                stringBuilder.append(nLocal).append(NEW_OBJECT_ARRAY)
                appendFrameTypes(stringBuilder, nLocal, local!!)
                stringBuilder.append("}, ").append(nStack).append(NEW_OBJECT_ARRAY)
                appendFrameTypes(stringBuilder, nStack, stack!!)
                stringBuilder.append('}')
            }
            Opcodes.F_APPEND -> {
                stringBuilder
                    .append(name)
                    .append(".visitFrame(Opcodes.F_APPEND,")
                    .append(nLocal)
                    .append(NEW_OBJECT_ARRAY)
                appendFrameTypes(stringBuilder, nLocal, local!!)
                stringBuilder.append("}, 0, null")
            }
            Opcodes.F_CHOP -> stringBuilder
                .append(name)
                .append(".visitFrame(Opcodes.F_CHOP,")
                .append(nLocal)
                .append(", null, 0, null")
            Opcodes.F_SAME -> stringBuilder.append(name)
                .append(".visitFrame(Opcodes.F_SAME, 0, null, 0, null")
            Opcodes.F_SAME1 -> {
                stringBuilder
                    .append(name)
                    .append(".visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] {")
                appendFrameTypes(stringBuilder, 1, stack!!)
                stringBuilder.append('}')
            }
            else -> throw IllegalArgumentException()
        }
        stringBuilder.append(");\n")
    }

    override fun compile(compile: AsmJsonCompile) {
        val cLocal = toCType(compile, local) as Array<out Any?>?
        val cStack = toCType(compile, stack) as Array<out Any?>?
        when (type) {
            Opcodes.F_NEW, Opcodes.F_FULL -> compile.getMethodVisitor(name!!)
                .visitFrame(type, nLocal, cLocal, nStack, cStack)
            Opcodes.F_APPEND -> compile.getMethodVisitor(name!!)
                .visitFrame(type, nLocal, cLocal, 0, null)
            Opcodes.F_CHOP -> compile.getMethodVisitor(name!!)
                .visitFrame(type, nLocal, null, 0, null)
            Opcodes.F_SAME -> compile.getMethodVisitor(name!!)
                .visitFrame(type, 0, null, 0, null)
            Opcodes.F_SAME1 -> compile.getMethodVisitor(name!!)
                .visitFrame(type, 0, null, 1, arrayOf(cStack!![0]))
            else -> throw IllegalArgumentException()
        }
    }

    init {
        when (type) {
            Opcodes.F_NEW, Opcodes.F_FULL -> {
                declareFrameTypes(asMifier, nLocal, local!!)
                declareFrameTypes(asMifier, nStack, stack!!)
            }
            Opcodes.F_APPEND -> declareFrameTypes(asMifier, nLocal, local!!)
            Opcodes.F_CHOP -> {
            }
            Opcodes.F_SAME -> {
            }
            Opcodes.F_SAME1 -> declareFrameTypes(asMifier, 1, stack!!)
            else -> {
            }
        }
        require(!(hasLable(local) || hasLable(stack))) { "Unkown Lables!!!" }
        local = toVType(asMifier, local as Array<Any?>?) as Array<Any>?
        stack = toVType(asMifier, stack as Array<Any?>?) as Array<Any>?
    }
}