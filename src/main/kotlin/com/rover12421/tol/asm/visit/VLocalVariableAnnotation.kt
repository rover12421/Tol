package com.rover12421.tol.asm.visit

import com.rover12421.tol.asm.json.ASMifierJson
import com.rover12421.tol.asm.json.AsmJsonCompile
import org.objectweb.asm.Label
import org.objectweb.asm.TypePath

class VLocalVariableAnnotation : VisitPrinter {
    @JvmField var typeRef = 0
    @JvmField var typePath: String? = null
    @JvmField var start: Array<NLabel>? = null
    @JvmField var end: Array<NLabel>? = null
    @JvmField  var index: IntArray = IntArray(0)
    @JvmField var descriptor: String? = null
    @JvmField var visible = false
    @JvmField var fromName: String? = null

    @Deprecated("")
    constructor() {
    }

    constructor(
        fromName: String?,
        asMifier: ASMifierJson?,
        typeRef: Int,
        typePath: TypePath?,
        start: Array<Label>?,
        end: Array<Label>?,
        index: IntArray,
        descriptor: String?,
        visible: Boolean
    ) : super(asMifier, true) {
        this.fromName = fromName
        this.typeRef = typeRef
        this.typePath = typePath?.toString()
        this.start = Label2Nlabel(asMifier, start)
        this.end = Label2Nlabel(asMifier, end)
        this.index = index
        this.descriptor = descriptor
        this.visible = visible
    }

    override fun toString(stringBuilder: StringBuilder) {
        stringBuilder
            .append("{\n")
            .append(name + id)
            .append(" = ")
            .append(fromName)
            .append(".visitLocalVariableAnnotation(")
            .append(typeRef)
        if (typePath == null) {
            stringBuilder.append(", null, ")
        } else {
            stringBuilder.append(", TypePath.fromString(\"").append(typePath).append("\"), ")
        }
        stringBuilder.append("new Label[] {")
        for (i in start!!.indices) {
            stringBuilder.append(if (i == 0) " " else ", ")
            appendLabel(stringBuilder, start!![i]!!)
        }
        stringBuilder.append(" }, new Label[] {")
        for (i in end!!.indices) {
            stringBuilder.append(if (i == 0) " " else ", ")
            appendLabel(stringBuilder, end!![i]!!)
        }
        stringBuilder.append(" }, new int[] {")
        for (i in index.indices) {
            stringBuilder.append(if (i == 0) " " else ", ").append(index[i])
        }
        stringBuilder.append(" }, ")
        appendConstant(stringBuilder, descriptor)
        stringBuilder.append(", ").append(visible).append(");\n")
        appendThisText(stringBuilder)
        appendVisitEnd(stringBuilder)
        nodes.add("}\n")
    }

    private fun appendVisitEnd(stringBuilder: StringBuilder) {
        stringBuilder.append(name).append(id).append(VISIT_END)
    }

    override fun compile(compile: AsmJsonCompile) {
        val annotationVisitor = compile.getMethodVisitor(name!!)
            .visitLocalVariableAnnotation(
                typeRef, TypePath.fromString(typePath),
                NLabel2label(compile, start),
                NLabel2label(compile, end),
                index, descriptor, visible
            )
        compile.put(name + id, annotationVisitor)
        compileThis(compile)
        annotationVisitor.visitEnd()
    }
}