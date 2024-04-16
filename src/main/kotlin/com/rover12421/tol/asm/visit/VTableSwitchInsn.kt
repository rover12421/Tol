package com.rover12421.asmjson.visit

import com.rover12421.asmjson.ASMifierJson
import com.rover12421.asmjson.compile.AsmJsonCompile
import org.objectweb.asm.Label

class VTableSwitchInsn : VisitPrinter {
    @JvmField var min = 0
    @JvmField var max = 0
    @JvmField var dflt: NLabel? = null
    @JvmField var labels: Array<NLabel>? = null

    @Deprecated("")
    constructor() {
    }

    constructor(
        asMifier: ASMifierJson?,
        min: Int,
        max: Int,
        dflt: Label?,
        labels: Array<Label>?
    ) : super(asMifier) {
        this.min = min
        this.max = max
        this.labels = Label2Nlabel(asMifier, labels)
        this.dflt = declareLabel(asMifier, dflt!!)
    }

    override fun toString(stringBuilder: StringBuilder) {
        for (i in labels!!.indices) {
            stringBuilder.append(labels!![i])
        }
        stringBuilder.append(dflt)
        stringBuilder
            .append(name)
            .append(".visitTableSwitchInsn(")
            .append(min)
            .append(", ")
            .append(max)
            .append(", ")
        appendLabel(stringBuilder, dflt!!)
        stringBuilder.append(", new Label[] {")
        for (i in labels!!.indices) {
            stringBuilder.append(if (i == 0) " " else ", ")
            appendLabel(stringBuilder, labels!![i]!!)
        }
        stringBuilder.append(END_ARRAY)
    }

    override fun compile(compile: AsmJsonCompile) {
        compile.getMethodVisitor(name!!).visitTableSwitchInsn(
                min, max,
                NLabel2label(compile, dflt),
                *NLabel2label(compile, labels)?: emptyArray()
            )
    }
}