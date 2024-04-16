package com.rover12421.tol.asm.json

import com.rover12421.tol.asm.visit.*
import org.objectweb.asm.*
import org.objectweb.asm.util.Printer

/**
 * A [Printer] that prints the ASM code to generate the classes if visits.
 */
class ASMifierJson protected constructor(
    api: Int, // the ASM API version implemented by this class.
    @JvmField val name: String?, // The name of the visitor variable in the produced code.
    @JvmField val id: Int //  The identifier of the annotation visitor variable in the produced code.
) : Printer(api) {

    /**
     * The name of the Label variables in the produced code.
     */
    @JvmField
    var labelNames: MutableMap<Label, String> = mutableMapOf()

    /**
     * Constructs a new [ASMifierJson]. *Subclasses must not use this constructor*. Instead,
     * they must use the [.ASMifierJson] version.
     *
     * @throws IllegalStateException If a subclass calls this constructor.
     */
    constructor() : this(AsmJsonConstant.DefaultAsmApiVersion, VisitPrinter.CLASSWRITER, 0) {
        check(javaClass == ASMifierJson::class.java)
    }

    // -----------------------------------------------------------------------------------------------
    // Classes
    // -----------------------------------------------------------------------------------------------
    lateinit var vClass: VClass

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<String>?
    ) {
        vClass = VClass(this, version, access, name, signature, superName, interfaces)
    }

    override fun visitSource(file: String, debug: String?) {
        text.add(VSource(this, file, debug))
    }

    override fun visitModule(name: String, flags: Int, version: String): ASMifierJson {
        val asmifier = createASMifier(VisitPrinter.MODULEVISITOR, 0)
        text.add(VModule(this.name, asmifier, name, flags, version))
        return asmifier
    }

    //    @Override
    //    public void visitNestHostExperimental(final String nestHost) {
    //        text.add(new VNestHostExperimental(this, nestHost));
    //    }
    override fun visitOuterClass(
        owner: String?,
        name: String?,
        descriptor: String?
    ) {
        text.add(VOuterClass(this, owner, name, descriptor))
    }

    override fun visitClassAnnotation(descriptor: String, visible: Boolean): ASMifierJson {
        return visitAnnotation(descriptor, visible)
    }

    override fun visitClassTypeAnnotation(
        typeRef: Int, typePath: TypePath, descriptor: String, visible: Boolean
    ): ASMifierJson {
        return visitTypeAnnotation(typeRef, typePath, descriptor, visible)
    }

    override fun visitClassAttribute(attribute: Attribute) {
        visitAttribute(attribute)
    }

//    @Override
//    public void visitNestMemberExperimental(final String nestMember) {
//        text.add(VNestMemberExperimental(this, nestMember));
//    }

    override fun visitNestMember(nestMember: String?) {
        text.add(VNestMember(this, nestMember))
    }

    override fun visitPermittedSubclass(permittedSubclass: String?) {
        text.add(VPermittedSubclass(this, permittedSubclass))
    }

    override fun visitInnerClass(
        name: String, outerName: String?, innerName: String?, access: Int
    ) {
        text.add(VInnerClass(this, name, outerName, innerName, access))
    }

    override fun visitField(
        access: Int,
        name: String,
        descriptor: String,
        signature: String?,
        value: Any?
    ): ASMifierJson {
        val asmifier = createASMifier(VisitPrinter.FIELDVISITOR, 0)
        text.add(VField(this.name!!, asmifier, access, name, descriptor, signature, value))
        return asmifier
    }

    override fun visitMethod(
        access: Int,
        name: String,
        descriptor: String,
        signature: String?,
        exceptions: Array<String>?
    ): ASMifierJson {
        stringBuilder.setLength(0)
        val asmifier = createASMifier(VisitPrinter.METHODVISITOR, 0)
        text.add(VMethod(this.name, asmifier, access, name, descriptor, signature, exceptions))
        return asmifier
    }

    override fun visitClassEnd() {}

    // -----------------------------------------------------------------------------------------------
    // Modules
    // -----------------------------------------------------------------------------------------------
    override fun visitMainClass(mainClass: String) {
        text.add(VMainClass(this, mainClass))
    }

    override fun visitPackage(packaze: String) {
        text.add(VPackage(this, packaze))
    }

    override fun visitRequire(module: String, access: Int, version: String) {
        text.add(VRequire(this, module, access, version))
    }

    override fun visitExport(packaze: String, access: Int, vararg modules: String) {
        text.add(VExport(this, packaze, access, modules as Array<String>))
    }

    override fun visitOpen(packaze: String, access: Int, vararg modules: String) {
        text.add(VOpen(this, packaze, access, modules as Array<String>))
    }

    override fun visitUse(service: String) {
        text.add(VUse(this, service))
    }

    override fun visitProvide(service: String, vararg providers: String) {
        text.add(VProvide(this, service, providers as Array<String>))
    }

    override fun visitModuleEnd() {}

    // -----------------------------------------------------------------------------------------------
    // Annotations
    // -----------------------------------------------------------------------------------------------
    override fun visit(name: String?, value: Any?) {
        text.add(VVisit(this, name, value))
    }

    override fun visitEnum(name: String?, descriptor: String?, value: String?) {
        text.add(VEnum(this, name, descriptor, value))
    }

    override fun visitAnnotation(name: String, descriptor: String): ASMifierJson {
        val asmifier = createASMifier(VisitPrinter.ANNOTATION_VISITOR, id + 1)
        text.add(VAnnotation(asmifier, name, descriptor))
        return asmifier
    }

    override fun visitArray(name: String?): ASMifierJson {
        val asmifier = createASMifier(VisitPrinter.ANNOTATION_VISITOR, id + 1)
        text.add(VArray(asmifier, name))
        return asmifier
    }

    override fun visitAnnotationEnd() {}

    // -----------------------------------------------------------------------------------------------
    // Fields
    // -----------------------------------------------------------------------------------------------
    override fun visitFieldAnnotation(descriptor: String, visible: Boolean): ASMifierJson {
        return visitAnnotation(descriptor, visible)
    }

    override fun visitFieldTypeAnnotation(
        typeRef: Int, typePath: TypePath, descriptor: String, visible: Boolean
    ): ASMifierJson {
        return visitTypeAnnotation(typeRef, typePath, descriptor, visible)
    }

    override fun visitFieldAttribute(attribute: Attribute) {
        visitAttribute(attribute)
    }

    override fun visitFieldEnd() {}

    // -----------------------------------------------------------------------------------------------
    // Methods
    // -----------------------------------------------------------------------------------------------
    override fun visitParameter(parameterName: String, access: Int) {
        text.add(VParameter(this, parameterName, access))
    }

    override fun visitAnnotationDefault(): ASMifierJson {
        val asmifier = createASMifier(VisitPrinter.ANNOTATION_VISITOR, 0)
        text.add(VAnnotationDefault(asmifier, name))
        return asmifier
    }

    override fun visitMethodAnnotation(descriptor: String, visible: Boolean): ASMifierJson {
        return visitAnnotation(descriptor, visible)
    }

    override fun visitMethodTypeAnnotation(
        typeRef: Int, typePath: TypePath, descriptor: String, visible: Boolean
    ): ASMifierJson {
        return visitTypeAnnotation(typeRef, typePath, descriptor, visible)
    }

    override fun visitAnnotableParameterCount(parameterCount: Int, visible: Boolean): ASMifierJson {
        text.add(VAnnotableParameterCount(this, parameterCount, visible))
        return this
    }

    override fun visitParameterAnnotation(
        parameter: Int, descriptor: String, visible: Boolean
    ): ASMifierJson {
        val asmifier = createASMifier(VisitPrinter.ANNOTATION_VISITOR, 0)
        text.add(VParameterAnnotation(name, asmifier, parameter, descriptor, visible))
        return asmifier
    }

    override fun visitMethodAttribute(attribute: Attribute) {
        visitAttribute(attribute)
    }

    override fun visitCode() {
        text.add(VCode(this))
    }

    override fun visitFrame(
        type: Int,
        nLocal: Int,
        local: Array<Any>,
        nStack: Int,
        stack: Array<Any>
    ) {
//        text.add(new VFrame(this, type, nLocal, local, nStack, stack));
    }

    override fun visitInsn(opcode: Int) {
        text.add(VInsn(this, opcode))
    }

    override fun visitIntInsn(opcode: Int, operand: Int) {
        text.add(VIntInsn(this, opcode, operand))
    }

    override fun visitVarInsn(opcode: Int, `var`: Int) {
        text.add(VVarInsn(this, opcode, `var`))
    }

    override fun visitTypeInsn(opcode: Int, type: String) {
        text.add(VTypeInsn(this, opcode, type))
    }

    override fun visitFieldInsn(
        opcode: Int, owner: String, name: String, descriptor: String
    ) {
        text.add(VFieldInsn(this, opcode, owner, name, descriptor))
    }

    @Deprecated("")
    override fun visitMethodInsn(
        opcode: Int, owner: String, name: String, descriptor: String
    ) {
        if (api >= Opcodes.ASM5) {
            super.visitMethodInsn(opcode, owner, name, descriptor)
            return
        }
        doVisitMethodInsn(opcode, owner, name, descriptor, opcode == Opcodes.INVOKEINTERFACE)
    }

    override fun visitMethodInsn(
        opcode: Int,
        owner: String,
        name: String,
        descriptor: String,
        isInterface: Boolean
    ) {
        if (api < Opcodes.ASM5) {
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
            return
        }
        doVisitMethodInsn(opcode, owner, name, descriptor, isInterface)
    }

    private fun doVisitMethodInsn(
        opcode: Int,
        owner: String,
        name: String,
        descriptor: String,
        isInterface: Boolean
    ) {
        text.add(VMethodInsn(this, opcode, owner, name, descriptor, isInterface))
    }

    override fun visitInvokeDynamicInsn(
        name: String,
        descriptor: String,
        bootstrapMethodHandle: Handle,
        vararg bootstrapMethodArguments: Any?
    ) {
        text.add(VInvokeDynamicInsn(this, name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments as Array<Any?>))
    }

    override fun visitJumpInsn(opcode: Int, label: Label) {
        text.add(VJumpInsn(this, opcode, label))
    }

    override fun visitLabel(label: Label) {
        text.add(VLabel(this, label))
    }

    override fun visitLdcInsn(value: Any) {
        text.add(VLdcInsn(this, value))
    }

    override fun visitIincInsn(`var`: Int, increment: Int) {
        text.add(VIincInsn(this, `var`, increment))
    }

    override fun visitTableSwitchInsn(
        min: Int, max: Int, dflt: Label, vararg labels: Label
    ) {
        text.add(VTableSwitchInsn(this, min, max, dflt, labels as Array<Label>))
    }

    override fun visitLookupSwitchInsn(
        dflt: Label,
        keys: IntArray,
        labels: Array<Label>
    ) {
        text.add(VLookupSwitchInsn(this, dflt, keys, labels))
    }

    override fun visitMultiANewArrayInsn(descriptor: String, numDimensions: Int) {
        text.add(VMultiANewArrayInsn(this, descriptor, numDimensions))
    }

    override fun visitInsnAnnotation(
        typeRef: Int, typePath: TypePath, descriptor: String, visible: Boolean
    ): ASMifierJson {
        return visitTypeAnnotation("visitInsnAnnotation", typeRef, typePath, descriptor, visible)
    }

    override fun visitTryCatchBlock(
        start: Label,
        end: Label,
        handler: Label,
        type: String?
    ) {
        text.add(VTryCatchBlock(this, start, end, handler, type))
    }

    override fun visitTryCatchAnnotation(
        typeRef: Int, typePath: TypePath, descriptor: String, visible: Boolean
    ): ASMifierJson {
        return visitTypeAnnotation("visitTryCatchAnnotation", typeRef, typePath, descriptor, visible)
    }

    override fun visitLocalVariable(
        name: String,
        descriptor: String,
        signature: String?,
        start: Label,
        end: Label,
        index: Int
    ) {
        text.add(VLocalVariable(this, name, descriptor, signature, start, end, index))
    }

    override fun visitLocalVariableAnnotation(
        typeRef: Int,
        typePath: TypePath,
        start: Array<Label>?,
        end: Array<Label>?,
        index: IntArray,
        descriptor: String,
        visible: Boolean
    ): Printer {
        val asmifier = createASMifier(VisitPrinter.ANNOTATION_VISITOR, 0)
        text.add(
            VLocalVariableAnnotation(
                name, asmifier, typeRef,
                typePath, start, end, index, descriptor, visible
            )
        )
        return asmifier
    }

    override fun visitLineNumber(line: Int, start: Label) {
        text.add(VLineNumber(this, line, start))
    }

    override fun visitMaxs(maxStack: Int, maxLocals: Int) {
        text.add(VMaxs(this, maxStack, maxLocals))
    }

    override fun visitMethodEnd() {}
    // -----------------------------------------------------------------------------------------------
    // Common methods
    // -----------------------------------------------------------------------------------------------
    /**
     * Visits a class, field or method annotation.
     *
     * @param descriptor the class descriptor of the annotation class.
     * @param visible    <tt>true</tt> if the annotation is visible at runtime.
     * @return a new [ASMifierJson] to visit the annotation values.
     */
    fun visitAnnotation(descriptor: String?, visible: Boolean): ASMifierJson {
        val asmifier = createASMifier(VisitPrinter.ANNOTATION_VISITOR, 0)
        text.add(VAnnotation(name, asmifier, descriptor, visible))
        return asmifier
    }

    /**
     * Visits a class, field or method type annotation.
     *
     * @param typeRef    a reference to the annotated type. The sort of this type reference must be
     * [org.objectweb.asm.TypeReference.FIELD]. See [org.objectweb.asm.TypeReference].
     * @param typePath   the path to the annotated type argument, wildcard bound, array element type, or
     * static inner type within 'typeRef'. May be <tt>null</tt> if the annotation targets
     * 'typeRef' as a whole.
     * @param descriptor the class descriptor of the annotation class.
     * @param visible    <tt>true</tt> if the annotation is visible at runtime.
     * @return a new [ASMifierJson] to visit the annotation values.
     */
    fun visitTypeAnnotation(
        typeRef: Int, typePath: TypePath?, descriptor: String?, visible: Boolean
    ): ASMifierJson {
        return visitTypeAnnotation("visitTypeAnnotation", typeRef, typePath, descriptor, visible)
    }

    /**
     * Visits a class, field, method, instruction or try catch block type annotation.
     *
     * @param method     the name of the visit method for this type of annotation.
     * @param typeRef    a reference to the annotated type. The sort of this type reference must be
     * [org.objectweb.asm.TypeReference.FIELD]. See [org.objectweb.asm.TypeReference].
     * @param typePath   the path to the annotated type argument, wildcard bound, array element type, or
     * static inner type within 'typeRef'. May be <tt>null</tt> if the annotation targets
     * 'typeRef' as a whole.
     * @param descriptor the class descriptor of the annotation class.
     * @param visible    <tt>true</tt> if the annotation is visible at runtime.
     * @return a new [ASMifierJson] to visit the annotation values.
     */
    fun visitTypeAnnotation(
        method: String?,
        typeRef: Int,
        typePath: TypePath?,
        descriptor: String?,
        visible: Boolean
    ): ASMifierJson {
        val asmifier = createASMifier(VisitPrinter.ANNOTATION_VISITOR, 0)
        text.add(
            VTypeAnnotation(
                name, asmifier, method, typeRef, typePath,
                descriptor, visible
            )
        )
        return asmifier
    }

    /**
     * Visit a class, field or method attribute.
     *
     * @param attribute an attribute.
     */
    fun visitAttribute(attribute: Attribute) {
        text.add(VAttribute(this, attribute))
    }
    // -----------------------------------------------------------------------------------------------
    // Utility methods
    // -----------------------------------------------------------------------------------------------
    /**
     * Constructs a new [ASMifierJson].
     *
     * @param visitorVariableName the name of the visitor variable in the produced code.
     * @param annotationVisitorId identifier of the annotation visitor variable in the produced code.
     * @return a new [ASMifierJson].
     */
    protected fun createASMifier(
        visitorVariableName: String?, annotationVisitorId: Int
    ): ASMifierJson {
        if (this.javaClass != ASMifierJson::class.java) {
            throw RuntimeException("继承类必须重写该方法")
        }
        return ASMifierJson(AsmJsonConstant.DefaultAsmApiVersion, visitorVariableName, annotationVisitorId)
    }

}