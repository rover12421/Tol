package com.rover12421.tol.asm.visit

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.rover12421.tol.asm.json.ASMifierJson
import com.rover12421.tol.asm.json.AsmJsonCompile
import com.rover12421.tol.asm.visit.*
import org.objectweb.asm.*
import org.objectweb.asm.util.Printer
import java.util.*

abstract class VisitPrinter {
    companion object {
        const val CLASSWRITER = "classWriter"
        const val FIELDVISITOR = "fieldVisitor"
        const val METHODVISITOR = "methodVisitor"
        const val ANNOTATIONVISITOR0 = "annotationVisitor0"
        const val DUMPMETHODNAME = "dump"
        const val MODULEVISITOR = "moduleVisitor"
        @JvmField
        var DEBUG = false
        private val OPCODES = Printer.OPCODES.toList()

        @JvmStatic
        fun toOpCodeStr(opcode: Int): String {
            return OPCODES[opcode]
        }

        @JvmStatic
        fun toOpCode(opcode: String): Int {
            return OPCODES.indexOf(opcode)
        }

        /**
         * A pseudo access flag used to distinguish class access flags.
         */
        const val ACCESS_CLASS = 0x40000

        /**
         * A pseudo access flag used to distinguish field access flags.
         */
        const val ACCESS_FIELD = 0x80000

        /**
         * A pseudo access flag used to distinguish inner class flags.
         */
        const val ACCESS_INNER = 0x100000

        /**
         * A pseudo access flag used to distinguish module requires / exports flags.
         */
        const val ACCESS_MODULE = 0x200000
        const val ANNOTATION_VISITOR = "annotationVisitor"
        const val NEW_OBJECT_ARRAY = ", new Object[] {"
        const val END_ARRAY = " });\n"
        const val END_PARAMETERS = ");\n\n"
        const val VISIT_END = ".visitEnd();\n"
        @JvmField val CLASS_VERSIONS = mapOf(
            Opcodes.V1_1 to "V1_1",
            Opcodes.V1_2 to "V1_2",
            Opcodes.V1_3 to "V1_3",
            Opcodes.V1_4 to "V1_4",
            Opcodes.V1_5 to "V1_5",
            Opcodes.V1_6 to "V1_6",
            Opcodes.V1_7 to "V1_7",
            Opcodes.V1_8 to "V1_8",
            Opcodes.V9 to "V9",
            Opcodes.V10 to "V10",
            Opcodes.V11 to "V11"
        )

        /**
         * Appends a quoted string to the given string builder.
         *
         * @param stringBuilder the buffer where the string must be added.
         * @param string the string to be added.
         */
        @JvmStatic
        fun appendString(stringBuilder: StringBuilder, string: String?) {
            Printer.appendString(stringBuilder, string)
        }
    }

    @JvmField var id = 0
    @JvmField var name: String? = null

    @JvmField
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    var nodes: MutableList<Any> = mutableListOf()

    @JvmOverloads
    constructor(asMifier: ASMifierJson? = null, setNodes: Boolean = false) {
        if (asMifier != null) {
            name = asMifier.name
            id = asMifier.id
            if (setNodes && asMifier.text != null) {
                nodes = asMifier.text
            }
        }
    }

    constructor(name: String?, id: Int) {
        this.name = name
        this.id = id
        nodes = ArrayList()
    }

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        toString(stringBuilder)
        return stringBuilder.toString()
    }

    abstract fun toString(stringBuilder: StringBuilder)
    abstract fun compile(compile: AsmJsonCompile)
    protected fun appendAccessFlags(stringBuilder: StringBuilder, accessFlags: Int) {
        var isEmpty = true
        if (accessFlags and Opcodes.ACC_PUBLIC != 0) {
            stringBuilder.append("ACC_PUBLIC")
            isEmpty = false
        }
        if (accessFlags and Opcodes.ACC_PRIVATE != 0) {
            stringBuilder.append("ACC_PRIVATE")
            isEmpty = false
        }
        if (accessFlags and Opcodes.ACC_PROTECTED != 0) {
            stringBuilder.append("ACC_PROTECTED")
            isEmpty = false
        }
        if (accessFlags and Opcodes.ACC_FINAL != 0) {
            if (!isEmpty) {
                stringBuilder.append(" | ")
            }
            if (accessFlags and ACCESS_MODULE == 0) {
                stringBuilder.append("ACC_FINAL")
            } else {
                stringBuilder.append("ACC_TRANSITIVE")
            }
            isEmpty = false
        }
        if (accessFlags and Opcodes.ACC_STATIC != 0) {
            if (!isEmpty) {
                stringBuilder.append(" | ")
            }
            stringBuilder.append("ACC_STATIC")
            isEmpty = false
        }
        if (accessFlags and (Opcodes.ACC_SYNCHRONIZED or Opcodes.ACC_SUPER or Opcodes.ACC_TRANSITIVE)
            != 0
        ) {
            if (!isEmpty) {
                stringBuilder.append(" | ")
            }
            if (accessFlags and ACCESS_CLASS == 0) {
                if (accessFlags and ACCESS_MODULE == 0) {
                    stringBuilder.append("ACC_SYNCHRONIZED")
                } else {
                    stringBuilder.append("ACC_TRANSITIVE")
                }
            } else {
                stringBuilder.append("ACC_SUPER")
            }
            isEmpty = false
        }
        if (accessFlags and (Opcodes.ACC_VOLATILE or Opcodes.ACC_BRIDGE or Opcodes.ACC_STATIC_PHASE)
            != 0
        ) {
            if (!isEmpty) {
                stringBuilder.append(" | ")
            }
            if (accessFlags and ACCESS_FIELD == 0) {
                if (accessFlags and ACCESS_MODULE == 0) {
                    stringBuilder.append("ACC_BRIDGE")
                } else {
                    stringBuilder.append("ACC_STATIC_PHASE")
                }
            } else {
                stringBuilder.append("ACC_VOLATILE")
            }
            isEmpty = false
        }
        if (accessFlags and Opcodes.ACC_VARARGS != 0
            && accessFlags and (ACCESS_CLASS or ACCESS_FIELD) == 0
        ) {
            if (!isEmpty) {
                stringBuilder.append(" | ")
            }
            stringBuilder.append("ACC_VARARGS")
            isEmpty = false
        }
        if (accessFlags and Opcodes.ACC_TRANSIENT != 0 && accessFlags and ACCESS_FIELD != 0) {
            if (!isEmpty) {
                stringBuilder.append(" | ")
            }
            stringBuilder.append("ACC_TRANSIENT")
            isEmpty = false
        }
        if (accessFlags and Opcodes.ACC_NATIVE != 0
            && accessFlags and (ACCESS_CLASS or ACCESS_FIELD) == 0
        ) {
            if (!isEmpty) {
                stringBuilder.append(" | ")
            }
            stringBuilder.append("ACC_NATIVE")
            isEmpty = false
        }
        if (accessFlags and Opcodes.ACC_ENUM != 0
            && accessFlags and (ACCESS_CLASS or ACCESS_FIELD or ACCESS_INNER) != 0
        ) {
            if (!isEmpty) {
                stringBuilder.append(" | ")
            }
            stringBuilder.append("ACC_ENUM")
            isEmpty = false
        }
        if (accessFlags and Opcodes.ACC_ANNOTATION != 0
            && accessFlags and (ACCESS_CLASS or ACCESS_INNER) != 0
        ) {
            if (!isEmpty) {
                stringBuilder.append(" | ")
            }
            stringBuilder.append("ACC_ANNOTATION")
            isEmpty = false
        }
        if (accessFlags and Opcodes.ACC_ABSTRACT != 0) {
            if (!isEmpty) {
                stringBuilder.append(" | ")
            }
            stringBuilder.append("ACC_ABSTRACT")
            isEmpty = false
        }
        if (accessFlags and Opcodes.ACC_INTERFACE != 0) {
            if (!isEmpty) {
                stringBuilder.append(" | ")
            }
            stringBuilder.append("ACC_INTERFACE")
            isEmpty = false
        }
        if (accessFlags and Opcodes.ACC_STRICT != 0) {
            if (!isEmpty) {
                stringBuilder.append(" | ")
            }
            stringBuilder.append("ACC_STRICT")
            isEmpty = false
        }
        if (accessFlags and Opcodes.ACC_SYNTHETIC != 0) {
            if (!isEmpty) {
                stringBuilder.append(" | ")
            }
            stringBuilder.append("ACC_SYNTHETIC")
            isEmpty = false
        }
        if (accessFlags and Opcodes.ACC_DEPRECATED != 0) {
            if (!isEmpty) {
                stringBuilder.append(" | ")
            }
            stringBuilder.append("ACC_DEPRECATED")
            isEmpty = false
        }
        if (accessFlags and (Opcodes.ACC_MANDATED or Opcodes.ACC_MODULE) != 0) {
            if (!isEmpty) {
                stringBuilder.append(" | ")
            }
            if (accessFlags and ACCESS_CLASS == 0) {
                stringBuilder.append("ACC_MANDATED")
            } else {
                stringBuilder.append("ACC_MODULE")
            }
            isEmpty = false
        }
        if (isEmpty) {
            stringBuilder.append('0')
        }
    }

    protected fun appendThisText(stringBuilder: StringBuilder) {
        for (obj in nodes) {
            stringBuilder.append(obj)
        }
    }

    protected fun compileThis(compile: AsmJsonCompile) {
        for (obj in nodes) {
            if (obj is VisitPrinter) {
                obj.compile(compile)
            }
        }
    }

    protected fun appendConstant(stringBuilder: StringBuilder, value: Any?) {
        if (value == null) {
            stringBuilder.append("null")
        } else if (value is String) {
            appendString(stringBuilder, value as String?)
        } else if (value is NAbs) {
            stringBuilder.append(value)
        }
    }

    protected fun declareFrameTypes(
        asMifier: ASMifierJson?,
        nTypes: Int,
        frameTypes: Array<Any>
    ) {
        for (i in 0 until nTypes) {
            if (frameTypes[i] is Label) {
                frameTypes[i] = declareLabel(asMifier, frameTypes[i] as Label)
            }
        }
    }

    protected fun declareLabel(asMifier: ASMifierJson?, label: Label): NLabel {
        var labelName = asMifier!!.labelNames[label]
        var first = false
        if (labelName == null) {
            labelName = "label" + asMifier.labelNames.size
            asMifier.labelNames.put(label, labelName)
            first = true
        }
        return NLabel(labelName, first)
    }

    protected fun hasLable(objs: Array<Any>?): Boolean {
        if (objs == null) {
            return false
        }
        for (i in objs.indices) {
            if (objs[i] is Label) {
                return true
            }
        }
        return false
    }

    protected fun appendLables(stringBuilder: StringBuilder, objs: Array<Any>?) {
        if (objs == null) {
            return
        }
        for (i in objs.indices) {
            if (objs[i] is NLabel) {
                stringBuilder.append(objs[i])
            }
        }
    }

    protected fun appendFrameTypes(
        stringBuilder: StringBuilder,
        nTypes: Int,
        frameTypes: Array<Any>
    ) {
        for (i in 0 until nTypes) {
            if (i > 0) {
                stringBuilder.append(", ")
            }
            if (frameTypes[i] is String) {
                appendConstant(stringBuilder, frameTypes[i])
            } else if (frameTypes[i] is NInteger) {
                when ((frameTypes[i] as NInteger).value) {
                    0 -> stringBuilder.append("Opcodes.TOP")
                    1 -> stringBuilder.append("Opcodes.INTEGER")
                    2 -> stringBuilder.append("Opcodes.FLOAT")
                    3 -> stringBuilder.append("Opcodes.DOUBLE")
                    4 -> stringBuilder.append("Opcodes.LONG")
                    5 -> stringBuilder.append("Opcodes.NULL")
                    6 -> stringBuilder.append("Opcodes.UNINITIALIZED_THIS")
                    else -> throw IllegalArgumentException()
                }
            } else {
                appendLabel(stringBuilder, frameTypes[i] as NLabel)
            }
        }
    }

    protected fun appendLabel(stringBuilder: StringBuilder, label: NLabel) {
        stringBuilder.append(label.labelName)
    }

    fun Label2Nlabel(
        asMifierJson: ASMifierJson?,
        labels: Array<Label>?
    ): Array<NLabel>? {
        if (labels == null) {
            return null
        }
        return Array(labels.size) {i-> declareLabel(asMifierJson, labels[i])}
    }

    fun toVType(objects: Array<Any?>?): Array<Any?>? {
        return toVType(null, objects)
    }

    fun toVType(asMifier: ASMifierJson?, objects: Array<Any?>?): Array<Any?>? {
        if (objects == null) {
            return null
        }
        return Array(objects.size) {i -> toVType(asMifier, objects[i])}
    }

    /**
     * 原生对象到包装对象
     * @param asMifier
     * @param any
     * @return
     */
    fun toVType(asMifier: ASMifierJson?, any: Any?): Any? {
        return if (any == null || any is String) {
            any
        } else if (any is Type) {
            NType(any)
        } else if (any is Handle) {
            NHandle(any)
        } else if (any is ConstantDynamic) {
            NConstantDynamic(any)
        } else if (any is Byte) {
            NByte(any)
        } else if (any is Boolean) {
            NBoolean(any)
        } else if (any is Short) {
            NShort(any)
        } else if (any is Char) {
            NCharacter(any)
        } else if (any is Int) {
            NInteger(any)
        } else if (any is Float) {
            NFloat(any)
        } else if (any is Long) {
            NLong(any)
        } else if (any is Double) {
            NDouble(any)
        } else if (any is ByteArray) {
            NByteArray(any)
        } else if (any is BooleanArray) {
            NBooleanArray(any)
        } else if (any is ShortArray) {
            NShortArray(any)
        } else if (any is CharArray) {
            NCharArray(any)
        } else if (any is IntArray) {
            NIntArray(any)
        } else if (any is LongArray) {
            NLongArray(any)
        } else if (any is FloatArray) {
            NFloatArray(any)
        } else if (any is DoubleArray) {
            NDoubleArray(any)
        } else if (any is Label) {
            declareLabel(asMifier, any)
        } else if (any is Array<*>) {
            if (any.isNotEmpty() && any[0] is Label) {
                Label2Nlabel(asMifier, any as Array<Label>)
            } else {
                any
            }
        } else {
            any
        }
    }

    fun NLabel2label(compile: AsmJsonCompile, nLabel: NLabel?): Label? {
        return compile.getLabel(nLabel)
    }

    fun NLabel2label(
        compile: AsmJsonCompile,
        nLabels: Array<NLabel>?
    ): Array<Label?>? {
        if (nLabels == null) {
            return null
        }
        val labels = arrayOfNulls<Label>(nLabels.size)
        for (i in nLabels.indices) {
            labels[i] = NLabel2label(compile, nLabels[i])
        }
        return labels
    }

    fun toCType(compile: AsmJsonCompile, objects: Array<Any?>?): Array<Any?>? {
        if (objects == null) {
            return null
        }
        val nobjs = arrayOfNulls<Any>(objects.size)
        for (i in objects.indices) {
            nobjs[i] = toCType(compile, objects[i])
        }
        return nobjs
    }

    /**
     * 包装对象到原生对象
     * @param compile
     * @param any
     * @return
     */
    fun toCType(compile: AsmJsonCompile, any: Any?): Any? {
        return if (any == null || any is String) {
            any
        } else if (any is NType) {
            Type.getType(any.descriptor)
        } else if (any is NHandle) {
            val nHandle = any
            Handle(
                nHandle.tag,
                nHandle.owner,
                nHandle.handleName,
                nHandle.descriptor,
                nHandle.isInterface
            )
        } else if (any is NConstantDynamic) {
            val nConstantDynamic = any
            ConstantDynamic(
                nConstantDynamic.constantDynamicName,
                nConstantDynamic.descriptor,
                toCType(compile, nConstantDynamic.bootstrapMethod) as Handle?,
                *toCType(compile, nConstantDynamic.bootstrapMethodArguments as Array<Any?>)?: emptyArray()
            )
        } else if (any is NByte) {
            java.lang.Byte.valueOf(any.value)
        } else if (any is NBoolean) {
            java.lang.Boolean.valueOf(any.value)
        } else if (any is NShort) {
            any.value
        } else if (any is NCharacter) {
            Character.valueOf(any.value)
        } else if (any is NInteger) {
            Integer.valueOf(any.value)
        } else if (any is NFloat) {
            java.lang.Float.valueOf(any.value)
        } else if (any is NLong) {
            java.lang.Long.valueOf(any.value)
        } else if (any is NDouble) {
            java.lang.Double.valueOf(any.value)
        } else if (any is NByteArray) {
            any.value
        } else if (any is NBooleanArray) {
            any.value
        } else if (any is NShortArray) {
            any.value
        } else if (any is NCharArray) {
            any.value
        } else if (any is NIntArray) {
            any.value
        } else if (any is NLongArray) {
            any.value
        } else if (any is NFloatArray) {
            any.value
        } else if (any is NDoubleArray) {
            any.value
        } else if (any is NLabel) {
            NLabel2label(compile, any)
        } else if (any is Array<*>) {
            if (any.isNotEmpty() && any[0] is NLabel) {
                NLabel2label(compile, any as Array<NLabel>)
            } else {
                any
            }
        } else {
            any
        }
    }

    fun addNodes(vararg ns: Any) {
        nodes.addAll(ns.toList())
    }

    fun addNodes(list: Collection<Any>) {
        nodes.addAll(list)
    }

    fun isTypeNode(type: Class<*>): Boolean {
        for (node in nodes) {
            if (type.isInstance(node)) {
                return true
            }
        }
        return false
    }

    fun removeNodes(vararg ns: Any) {
        nodes.removeAll(ns.toList())
    }

    fun removeNodes(list: Collection<Any>) {
        nodes.removeAll(list)
    }
}