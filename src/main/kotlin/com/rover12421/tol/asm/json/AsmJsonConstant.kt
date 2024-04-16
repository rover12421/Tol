package com.rover12421.tol.asm.json

import org.objectweb.asm.Opcodes

object AsmJsonConstant {
    /**
     * 默认jvm版本
     */
    const val DefaultJvmVersion = Opcodes.V1_8

    /**
     * 默认ASM API 版本
     */
    const val DefaultAsmApiVersion = Opcodes.ASM7

    const val JvmClass_Object = "java/lang/Object"
    const val JvmClass_String = "java/lang/String"
    const val Jvm_ConstructMethodName = "<init>"
    const val Jvm_StaticInitMethodName = "<clinit>"
    const val Jvm_EmptyMethodArgsVoid = "()V"
    const val Jvm_Signature_Object = "Ljava/lang/Object;"
    const val Jvm_Signature_String = "Ljava/lang/String;"
}