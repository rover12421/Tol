package com.rover12421.asmjson.visit

import com.rover12421.asmjson.ASMifierJson
import com.rover12421.asmjson.compile.AsmJsonCompile
import org.objectweb.asm.Attribute

class VAttribute : VisitPrinter {
    @JvmField var attribute: NAttribute? = null

    @Deprecated("")
    constructor() {
    }

    constructor(asMifier: ASMifierJson?, attribute: Attribute) : super(asMifier) {
        this.attribute = NAttribute(attribute)
    }

    override fun toString(stringBuilder: StringBuilder) {
//        stringBuilder.append("// ATTRIBUTE ").append(attribute.type).append('\n');
//        if (attribute instanceof ASMifiable) {
//            stringBuilder.append("{\n");
////            StringBuffer stringBuffer = new StringBuffer();
////            ((ASMifiable) attribute).asmify(stringBuffer, "attribute", labelNames);
////            stringBuilder.append(stringBuffer.toString());
//            stringBuilder.append(name).append(".visitAttribute(attribute);\n");
//            stringBuilder.append("}\n");
//        }
        throw RuntimeException("未实现")
    }

    override fun compile(compile: AsmJsonCompile) {
        throw RuntimeException("未实现")
        //        compile.getMethodVisitor(name)
//                .visitAttribute(new Attribute(attribute.type));
    }
}