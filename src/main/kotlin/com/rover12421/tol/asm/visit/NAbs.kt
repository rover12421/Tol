package com.rover12421.asmjson.visit

import com.rover12421.asmjson.compile.AsmJsonCompile

abstract class NAbs : VisitPrinter(null) {
    override fun compile(compile: AsmJsonCompile) {}
}