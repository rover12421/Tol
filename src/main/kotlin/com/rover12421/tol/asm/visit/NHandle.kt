package com.rover12421.asmjson.visit

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import org.objectweb.asm.Handle
import org.objectweb.asm.util.Printer

class NHandle @JsonCreator constructor(
    @JvmField @JsonProperty("tag") var tag: Int,
    @JvmField @JsonProperty("owner") var owner: String,
    @JvmField @JsonProperty("handleName") var handleName: String,
    @JvmField @JsonProperty("descriptor") var descriptor: String,
    @JvmField @JsonProperty("isInterface") var isInterface: Boolean
) : NAbs() {

    constructor(handle: Handle) : this(
        handle.tag,
        handle.owner,
        handle.name,
        handle.desc,
        handle.isInterface
    ) {
    }

    override fun toString(stringBuilder: StringBuilder) {
        stringBuilder.append("new Handle(")
        stringBuilder.append("Opcodes.").append(Printer.HANDLE_TAG[tag]).append(", \"")
        stringBuilder.append(owner).append("\", \"")
        stringBuilder.append(handleName).append("\", \"")
        stringBuilder.append(descriptor).append("\", ")
        stringBuilder.append(isInterface).append(")")
    }

}