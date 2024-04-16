package com.rover12421.asmjson.visit

import com.rover12421.asmjson.ASMifierJson
import com.rover12421.asmjson.compile.AsmJsonCompile
import com.rover12421.lotus.util.Log

class VMethod : VisitPrinter {
    @JvmField var access = 0
    @JvmField var methodName: String = ""
    @JvmField var descriptor: String = ""
    @JvmField var signature: String? = null
    @JvmField var exceptions: Array<String>? = null
    @JvmField var classWriter: String? = null

    @Deprecated("")
    constructor() {
    }

    constructor(
        classWriter: String?,
        asMifier: ASMifierJson?,
        access: Int,
        methodName: String,
        descriptor: String,
        signature: String?,
        exceptions: Array<String>?
    ) : super(asMifier, true) {
        this.classWriter = classWriter
        this.access = access
        this.methodName = methodName
        this.descriptor = descriptor
        this.signature = signature
        this.exceptions = exceptions
    }

    fun addVisitMaxs(vClass: VClass?) {
        addNodes(
            VMaxs.Companion.getInstance(this, 0, 0)
        )
    }

    override fun toString(stringBuilder: StringBuilder) {
        stringBuilder.append("{\n")
        stringBuilder.append("$name = $classWriter.visitMethod(")
        appendAccessFlags(stringBuilder, access)
        stringBuilder.append(", ")
        appendConstant(stringBuilder, methodName)
        stringBuilder.append(", ")
        appendConstant(stringBuilder, descriptor)
        stringBuilder.append(", ")
        appendConstant(stringBuilder, signature)
        stringBuilder.append(", ")
        if (exceptions != null && exceptions!!.size > 0) {
            stringBuilder.append("new String[] {")
            for (i in exceptions!!.indices) {
                stringBuilder.append(if (i == 0) " " else ", ")
                appendConstant(stringBuilder, exceptions!![i])
            }
            stringBuilder.append(" }")
        } else {
            stringBuilder.append("null")
        }
        stringBuilder.append(");\n")
        appendThisText(stringBuilder)
        appendVisitEnd(stringBuilder)
        stringBuilder.append("}\n")
    }

    private fun appendVisitEnd(stringBuilder: StringBuilder) {
        stringBuilder.append(name).append(VISIT_END)
    }

    override fun compile(compile: AsmJsonCompile) {
        compile.resetLables()
        val methodVisitor = compile.getClassWriter(classWriter!!)
            .visitMethod(access, methodName, descriptor, signature, exceptions)
        compile.put(name, methodVisitor)
//        Log.info("compile VMethod $methodName $descriptor");
        compileThis(compile)
        if (!isTypeNode(VMaxs::class.java)) {
            methodVisitor.visitMaxs(0, 0)
        }
        methodVisitor.visitEnd()
    }

    @Transient
    private var methodInsnList: List<VMethodInsn>? = null

    /**
     * 获取该方法中所有的方法调用节点
     * @return
     */
    val allMethodInsns: List<VMethodInsn>?
        get() {
            if (methodInsnList != null) return methodInsnList
            val list: MutableList<VMethodInsn> = ArrayList()
            for (node in nodes) {
                if (node is VMethodInsn) {
                    list.add(node)
                }
            }
            methodInsnList = list
            return methodInsnList
        }

    fun hashCallMethod(callClass: VClass, callMethod: VMethod, onlyChekClass: Boolean): Boolean {
        for (vMethodInsn in allMethodInsns!!) {
            if (vMethodInsn.owner == callClass.fullName) {
                if (onlyChekClass) {
                    return true
                } else if (vMethodInsn.methodName == callMethod.methodName && vMethodInsn.descriptor == callMethod.descriptor) {
                    return true
                }
            }
        }
        return false
    }

    fun hashCallMethod(callJvmClass: String): Boolean {
        for (vMethodInsn in allMethodInsns!!) {
            if (vMethodInsn.owner == callJvmClass) {
                return true
            }
        }
        return false
    }

    fun hashCallMethod(callJvmClass: String, methodName: String): Boolean {
        if (methodName == "*") {
            return hashCallMethod(callJvmClass)
        }
        for (vMethodInsn in allMethodInsns!!) {
            if (vMethodInsn.owner == callJvmClass && vMethodInsn.methodName == methodName) {
                return true
            }
        }
        return false
    }

    companion object {
        fun getInstance(
            vClass: VClass,
            access: Int,
            methodName: String,
            descriptor: String,
            signature: String?,
            exceptions: Array<String>?
        ): VMethod {
            val vMethod = VMethod(vClass.name, null, access, methodName, descriptor, signature, exceptions)
            vMethod.name = METHODVISITOR
            return vMethod
        }
    }
}