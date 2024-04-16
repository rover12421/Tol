package com.rover12421.tol.asm.visit

import com.rover12421.tol.asm.json.ASMifierJson
import com.rover12421.tol.asm.json.AsmJsonCompile
import org.objectweb.asm.Label

class VLookupSwitchInsn : VisitPrinter {
    @JvmField var dflt: NLabel? = null
    @JvmField var keys: IntArray = IntArray(0)
    @JvmField var labels: Array<NLabel>? = null

    @Deprecated("")
    constructor() {
    }

    constructor(
        asMifier: ASMifierJson?,
        dflt: Label?,
        keys: IntArray,
        labels: Array<Label>?
    ) : super(asMifier) {
        this.keys = keys
        this.labels = Label2Nlabel(asMifier, labels)
        this.dflt = declareLabel(asMifier, dflt!!)
    }

    override fun toString(stringBuilder: StringBuilder) {
        for (i in labels!!.indices) {
            stringBuilder.append(labels!![i])
        }
        stringBuilder.append(dflt)
        stringBuilder.append(name).append(".visitLookupSwitchInsn(")
        appendLabel(stringBuilder, dflt!!)
        stringBuilder.append(", new int[] {")
        for (i in keys.indices) {
            stringBuilder.append(if (i == 0) " " else ", ").append(keys[i])
        }
        stringBuilder.append(" }, new Label[] {")
        for (i in labels!!.indices) {
            stringBuilder.append(if (i == 0) " " else ", ")
            appendLabel(stringBuilder, labels!![i]!!)
        }
        stringBuilder.append(END_ARRAY)
    }

    override fun compile(compile: AsmJsonCompile) {
        compile.getMethodVisitor(name!!)
            .visitLookupSwitchInsn(NLabel2label(compile, dflt), keys, NLabel2label(compile, labels))
    }
}