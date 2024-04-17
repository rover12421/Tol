package com.rover12421.tol.asm.compile

import com.rover12421.tol.util.Log
import org.objectweb.asm.ClassWriter

class RsClassWriter(flags: Int, private val classLoader: ClassLoader) : ClassWriter(flags) {
    override fun getClassLoader(): ClassLoader {
        return classLoader
    }

    override fun getCommonSuperClass(type1: String, type2: String): String {
        val commonSuperClass = try {
            super.getCommonSuperClass(type1, type2)
        } catch (e: Exception) {
            Log.warn("getCommonSuperClass:($type1, $type2) -> ${e.message}")
            "java/lang/Object"
        }
//        Log.debug("getCommonSuperClass:($type1, $type2) -> $commonSuperClass")
        return commonSuperClass
    }
}