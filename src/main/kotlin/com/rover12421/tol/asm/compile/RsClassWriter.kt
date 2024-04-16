package com.rover12421.tol.asm.compile

import com.rover12421.tol.util.Log
import org.objectweb.asm.ClassWriter

class RsClassWriter(flags: Int, private val classLoader: ClassLoader) : ClassWriter(flags) {
    override fun getClassLoader(): ClassLoader {
        return classLoader
    }

    override fun getCommonSuperClass(type1: String, type2: String): String {
        val commonSuperClass = super.getCommonSuperClass(type1, type2)
        Log.info("getCommonSuperClass:($type1, $type2) -> $commonSuperClass")
//        return commonSuperClass
        return "java/lang/Object"
    }
}