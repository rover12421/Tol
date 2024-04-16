package com.rover12421.tol.asm.visit

import com.rover12421.tol.asm.visit.VisitPrinter
import com.rover12421.tol.asm.json.AsmJsonCompile

abstract class NAbs : VisitPrinter(null) {
    override fun compile(compile: AsmJsonCompile) {}
}