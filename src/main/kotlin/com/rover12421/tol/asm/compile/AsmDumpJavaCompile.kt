package com.rover12421.tol.asm.compile

import com.rover12421.tol.util.Log
import com.rover12421.tol.util.Log.info
import org.apache.commons.lang3.StringEscapeUtils
import org.apache.commons.lang3.StringUtils
import org.objectweb.asm.*
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import java.util.regex.Pattern

object AsmDumpJavaCompile {
    private val OpcodesVals: MutableMap<String?, Int?> = HashMap()
    var DEBUG = false

    @Throws(Exception::class)
    private fun getOpcodesVal(name: String?): Int {
        var name = name
        try {
            return name!!.toInt()
        } catch (ignore: NumberFormatException) {
        }
        if (name!!.startsWith("Opcodes.")) {
            name = name.substring(8)
        }
        var integer = OpcodesVals[name]
        if (integer == null) {
            integer =
                Opcodes::class.java.getField(name)[Opcodes::class.java] as Int
            OpcodesVals[name] = integer
        }
        return integer.toInt()
    }

    private val objs: MutableMap<String?, Any?> =
        HashMap()

    private fun getStringOrNull(str: String?): String? {
        return if (str == null || str == "null") {
            null
        } else if (str.startsWith("\"") && str.endsWith("\"")) {
            StringEscapeUtils.unescapeJava(StringUtils.substring(str, 1, -1))
        } else {
            str
        }
    }

    private fun isNull(str: String?): Boolean {
        return str == null || str == "null"
    }

    private fun isString(str: String?): Boolean {
        return str!!.startsWith("\"") && str.endsWith("\"")
    }

    @Throws(Exception::class)
    private fun toValue(
        list: List<Array<String?>?>,
        listIndex: Int,
        arrayIndex: Int
    ): Any? {
        val sp = list[listIndex]
        var swStr = sp!![arrayIndex]
        if (isNull(swStr)) {
            return null
        }
        if (isString(swStr)) {
            return getStringOrNull(swStr)
        }
        if (swStr!!.startsWith("Opcodes.")) {
            return getOpcodesVal(swStr)
        } else if (Pattern.matches("l\\d+", swStr) || Pattern.matches(
                "label\\d+",
                swStr
            )
        ) {
            return getLable(swStr)
        }
        var value: Any? = null
        var sp2: Array<String?>? = null
        if (list.size - 1 > listIndex) {
            sp2 = list[listIndex + 1]
        }
        if (swStr.contains("(")) {
            sp2 = swStr.split("\\(|\\)").toTypedArray()
            swStr = sp2[0]
        }
        if (sp2 == null && arrayIndex + 1 < sp.size) {
            sp2 = arrayOf(swStr, sp[arrayIndex + 1])
        }
        var sp2i1ByString: String? = null
        if (sp2 != null && sp2.size > 1) {
            sp2i1ByString = getStringOrNull(sp2[1])
        }
        when (swStr) {
            "Boolean.TRUE", "true" -> value = java.lang.Boolean.TRUE
            "Boolean.FALSE", "false" -> value = java.lang.Boolean.FALSE
            "new String[]" -> {
                val strings = arrayOfNulls<String>(sp2!!.size)
                var i = 0
                while (i < strings.size) {
                    strings[i] = getStringOrNull(sp2[i])
                    i++
                }
                value = strings
            }
            "new Object[]" -> {
                val objects = arrayOfNulls<Any>(sp2!!.size)
                var i = 0
                while (i < objects.size) {
                    objects[i] = toValue(list, listIndex + 1, i)
                    i++
                }
                value = objects
            }
            "new Label[]" -> {
                val labels = arrayOfNulls<Label>(sp2!!.size)
                var i = 0
                while (i < labels.size) {
                    labels[i] = getLable(sp2[i])
                    i++
                }
                value = labels
            }
            "new boolean[]" -> {
                val booleans = BooleanArray(sp2!!.size)
                var i = 0
                while (i < booleans.size) {
                    booleans[i] = java.lang.Boolean.valueOf(sp2[i])
                    i++
                }
                value = booleans
            }
            "new int[]" -> {
                val ints = IntArray(sp2!!.size)
                var i = 0
                while (i < ints.size) {
                    ints[i] = Integer.valueOf(sp2[i])
                    i++
                }
                value = ints
            }
            "Type.getType" -> value = Type.getType(sp2i1ByString)
            "new Integer" -> value = sp2i1ByString!!.toInt()
            "new Long" -> {
                if (sp2i1ByString!!.endsWith("L")) {
                    sp2i1ByString = StringUtils.substring(sp2i1ByString, 0, -1)
                }
                value = sp2i1ByString!!.toLong()
            }
            "new Double" -> value = sp2i1ByString!!.toDouble()
            "new Float" -> value = sp2i1ByString!!.toFloat()
            else -> throw RuntimeException("get Value unkown function call : $swStr")
        }
        return value
    }

    private fun getStringArray(str: String): Array<String?>? {
        var str: String? = str
        if (str == null || str == "null") {
            return null
        }
        str = if (str.startsWith("new String[]{")) {
            str.substring(13, str.length - 1)
        } else if (str.startsWith("new String[] {")) {
            str.substring(14, str.length - 1)
        } else {
            throw RuntimeException("getStringArray error : $str")
        }
        if (str.trim { it <= ' ' }.isEmpty()) {
            return arrayOf()
        }
        val split: Array<String?> = str.trim { it <= ' ' }.split(",").toTypedArray()
        for (i in split.indices) {
            split[i] = getStringOrNull(split[i]!!.trim { it <= ' ' })
        }
        return split
    }

    //    private static int[] getIntArray(String str) {
    //        if (str == null || str.equals("null")) {
    //            return null;
    //        }
    //        if (str.startsWith("new int[]{")) {
    //            str = str.substring(10, str.length()-1);
    //        } else if (str.startsWith("new int[] {")) {
    //            str = str.substring(11, str.length()-1);
    //        } else {
    //            throw new RuntimeException("getIntArray error : " + str);
    //        }
    //
    //        if (str.trim().isEmpty()) {
    //            return new int[]{};
    //        }
    //
    //        String[] split = str.split(",");
    //        int[] ints = new int[split.length];
    //        for (int i = 0; i < split.length; i++) {
    //            ints[i] = Integer.parseInt(split[i].trim());
    //        }
    //        return ints;
    //    }
    //
    //    private static Label[] getLableArray(String str) {
    //        if (str == null || str.equals("null")) {
    //            return null;
    //        }
    //        if (str.startsWith("new Label[]{")) {
    //            str = str.substring(12, str.length()-1);
    //        } else if (str.startsWith("new Label[] {")) {
    //            str = str.substring(13, str.length()-1);
    //        } else {
    //            throw new RuntimeException("getLableArray error : " + str);
    //        }
    //
    //        if (str.trim().isEmpty()) {
    //            return new Label[]{};
    //        }
    //
    //        String[] split = str.split(",");
    //        Label[] labels = new Label[split.length];
    //        for (int i = 0; i < split.length; i++) {
    //            labels[i] = getLable(split[i].trim());
    //        }
    //        return labels;
    //    }
    //
    //    private static Object[] getObjectArray(String str) throws Exception {
    //        if (str == null || str.equals("null")) {
    //            return null;
    //        }
    //
    //        if (str.startsWith("new Object[]{")) {
    //            str = StringUtils.substring(str, 13, -1);
    //        } else if (str.startsWith("new Object[] {")) {
    //            str = StringUtils.substring(str, 14, -1);
    //        } else {
    //            throw new RuntimeException("getObjectArray error : " + str);
    //        }
    //
    //        if (str.trim().isEmpty()) {
    //            return new Object[]{};
    //        }
    //
    //        String[] split = str.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
    //        trim(split);
    //        Object[] objects = new Object[split.length];
    //        for (int i = 0; i < split.length; i++) {
    //            String element = split[i].trim();
    //            if (element.startsWith("Opcodes.")) {
    //                objects[i] = getOpcodesVal(element);
    //            } else if (element.startsWith("\"")) {
    //                objects[i] = getStringOrNull(element);
    //            } else if (element.equals("true") || element.equals("fasle")) {
    //                objects[i] = Boolean.valueOf(element);
    //            } else if (element.startsWith("l")) {
    //                objects[i] = getLable(element);
    //            } else {
    //                throw new RuntimeException("getObjectArray find unkown type : " + element);
    //            }
    //        }
    //        return objects;
    //    }
    private fun trim(strArray: Array<String?>): Array<String?> {
        for (i in strArray.indices) {
            strArray[i] = strArray[i]!!.trim { it <= ' ' }
        }
        return strArray
    }

    @Throws(Exception::class)
    private fun getAccess(str: String?): Int {
        var access = 0
        if (str!!.contains("+")) {
            val accessStrs = str.split(" \\+ ").toTypedArray()
            for (accessStr in accessStrs) {
                access += getOpcodesVal(accessStr.trim { it <= ' ' })
            }
        } else {
            val accessStrs = str.split(" \\| ").toTypedArray()
            for (accessStr in accessStrs) {
                access = access or getOpcodesVal(accessStr.trim { it <= ' ' })
            }
        }
        return access
    }

    private fun getLable(labName: String?): Label? {
        return objs[labName] as Label?
    }

    private fun splitListFirst(
        list: MutableList<Array<String?>?>,
        spStr: String
    ) {
        val split = list[0]
        val split1: Array<String?> = split!![0]!!.split(spStr).toTypedArray()
        trim(split1)
        val sp3 = arrayOfNulls<String>(split.size + split1.size - 1)
        System.arraycopy(split1, 0, sp3, 0, split1.size)
        System.arraycopy(split, 1, sp3, split1.size, split.size - 1)
        list[0] = sp3
    }

    private fun splitLine2(line: String): List<Array<String?>?> {
        var line = line
        if (DEBUG) {
            Log.info("[splitLine2][line] : $line")
        }
        if (line.endsWith(");")) {
            line = StringUtils.substring(line, 0, -2)
        }
        val list: MutableList<Array<String?>?> = mutableListOf()
        val split = if (line.startsWith("mv.visitInvokeDynamicInsn(")
            || line.startsWith("methodVisitor.visitInvokeDynamicInsn(")
        ) {
            val index = line.indexOf("{")
            arrayOf(
                line.substring(0, index),
                StringUtils.substring(line, index + 1, -1)
            )
        } else {
            line.split("\\{(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)|}(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)")
                .dropLastWhile { it.isEmpty() }.toTypedArray()
        }
        for (str in split) {
            var useStr = str.trim { it <= ' ' }
            if (useStr.startsWith(",")) {
                useStr = useStr.substring(1)
            }
            val split2: Array<String?> = useStr.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)").toTypedArray()
            list.add(trim(split2))
        }

//        splitListFirst(list, "\\((?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)|\\)(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
//        splitListFirst(list, "\\=(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
//        splitListFirst(list, "\\((?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)|\\)");
        line = line.trim { it <= ' ' }
        if (line.startsWith("mv.visitLdcInsn(") && line["mv.visitLdcInsn(".length] == '"' && line[line.length - 1] == '"') {
            list[0] = arrayOf("mv.visitLdcInsn", line.substring("mv.visitLdcInsn(".length))
        } else if (line.startsWith("methodVisitor.visitLdcInsn(") && line["methodVisitor.visitLdcInsn(".length] == '"' && line[line.length - 1] == '"'
        ) {
            list[0] = arrayOf(
                "methodVisitor.visitLdcInsn",
                line.substring("methodVisitor.visitLdcInsn(".length)
            )
        } else {
            splitListFirst(list, "\\(|\\)|\\=")
        }
        for (i in list.indices) {
            val strs = list[i]
            if (strs!!.size == 1 && strs[0]!!.isEmpty()) {
                list[i] = arrayOf()
            }
        }
        while (true) {
            val last = list[list.size - 1]
            if (last!!.size == 1) {
                val s = last[0]!!.trim { it <= ' ' }
                if (s == ")" || s == "}") {
                    list.removeAt(list.size - 1)
                    continue
                }
            }
            break
        }
        if (DEBUG) {
            for (strings in list) {
                info("[splitLine2] : " + strings.contentToString())
            }
        }
        return list
    }

    //    private static String[] splitLine(String line) {
    //        System.out.println(line);
    //
    //        char lastCh = line.charAt(line.length()-1);
    //        while (lastCh == ')' || lastCh == '}' || lastCh == ';') {
    //            line = StringUtils.substring(line, 0, -1);
    //            lastCh = line.charAt(line.length()-1);
    //        }
    //
    //        String[] split1 = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
    //        String[] split2 = split1[0].split(" = |\\(");
    //        String[] split3 = split2[0].split(" ");
    //        String[] split = new String[split3.length + split1.length + split2.length -2];
    //
    //        System.arraycopy(split3, 0, split, 0, split3.length);
    //        System.arraycopy(split2, 1, split, split3.length, split2.length-1);
    //        System.arraycopy(split1, 1, split, split3.length+split2.length-1, split1.length -1);
    //
    //        List list = new ArrayList();
    //        String merge = null;
    //        boolean bmerge = false;
    //        for (int i = 0; i < split.length; i++) {
    //            String s = split[i];
    //            if (merge != null) {
    //                merge += "," + s;
    //            }
    //            if (s.trim().startsWith("new ") && s.contains("{")) {
    //                bmerge = true;
    //                if (merge != null) {
    //                    throw new RuntimeException("split find error");
    //                }
    //                merge = s;
    //            }
    //            if (bmerge && s.endsWith("}")) {
    //                bmerge = false;
    //                if (merge == null) {
    //                    throw new RuntimeException("split find error");
    //                }
    //                list.add(merge);
    //                merge = null;
    //                continue;
    //            }
    //
    //            if (merge == null) {
    //                list.add(s);
    //            }
    //        }
    //
    //        if (merge != null) {
    //            list.add(merge);
    //        }
    //
    //        split = (String[]) list.toArray(new String[list.size()]);
    //
    //        trim(split);
    //        System.out.println(Arrays.toString(split));
    //        return split;
    //    }
    private fun toInt(str: String?): Int {
        return str!!.trim { it <= ' ' }.toInt()
    }

    //    private static Object getVaule(String line, String[] split, int start) {
    //        Object value = getStringOrNull(split[start]);
    //        if (value == null || ((String) value).trim().isEmpty() || (split[start].startsWith("\"") && split[start].endsWith("\""))) {
    //            return value;
    //        }
    //
    //        if (split[start].startsWith("\"") && split[split.length-1].endsWith("\"")) {
    //            int sindex = line.indexOf(split[start]);
    //            value = StringUtils.substring(line, sindex+1, line.indexOf(split[split.length-1], sindex+1)) + StringUtils.substring(split[split.length-1], 0, -1);
    //            return value;
    //        }
    //
    //        String sp2[] = splitLine(split[start]);
    //
    //        String swStr = sp2[0];
    //        if (swStr.endsWith("new")) {
    //            swStr = sp2[1];
    //            if (sp2.length > 2) {
    //                if (sp2[2].startsWith("{") || sp2[2].startsWith("(")) {
    //                    sp2[2] = sp2[2].substring(1);
    //                }
    //            }
    //        }
    //
    //        int varlen = sp2.length - 2;
    //
    //        switch (swStr) {
    //            case "Boolean.TRUE":
    //                return Boolean.TRUE;
    //            case "Boolean.FALSE":
    //                return Boolean.FALSE;
    //            case "Type.getType":
    //                value = Type.getType(getStringOrNull(split[start+1]));
    //                break;
    //            case "Integer":
    //                value = new Integer(toInt(split[start+1]));
    //                break;
    //            case "Long":
    //                value = new Long(Long.parseLong(split[start+1]));
    //                break;
    //            case "Double":
    //                value = new Double(Double.parseDouble(getStringOrNull(sp2[2])));
    //                break;
    //            case "Float":
    //                value = new Float(Float.parseFloat(getStringOrNull(sp2[2])));
    //                break;
    //            case "boolean[]":
    //                boolean[] booleans = new boolean[varlen];
    //                for (int i = 0; i < varlen; i++) {
    //                    booleans[i] = Boolean.valueOf(sp2[2+i]);
    //                }
    //                break;
    //            default:
    //                throw new RuntimeException("get Value unkown function call : " + swStr);
    //        }
    //
    //        return value;
    //    }
    @Throws(Exception::class)
    fun compile2class(asmJavaFile: Path?, outClass: Path) {
        var bytes = Files.readAllBytes(asmJavaFile)
        val str = String(bytes)
        bytes = compile2class(str)
        if (outClass.parent != null) {
            Files.createDirectories(outClass.parent)
        }
        Files.write(outClass, bytes)
    }

    @Throws(Exception::class)
    fun compile2class(dumpJava: String): ByteArray {
        /**
         * 0 : 将不会自动进行计算。你必须自己计算帧、局部变量和操作数栈的大小
         * ClassWriter.COMPUTE_MAXS 局部变量和操作数栈的大小就会自动计算。但是，你仍然需要自己调用visitMaxs方法，尽管你可以使用任何参数：实际上这些参数会被忽略，然后重新计算。使用这个选项，你仍然需要计算帧的大小
         * ClassWriter.COMPUTE_FRAMES 所有的大小都将自动为你计算。你也不许要调用visitFrame方法，但是你仍然需要调用visitMaxs方法（参数将被忽略然后重新计算）
         *
         * COMPUTE_MAXS 选项将使得 ClassWriter 慢10%，使用 COMPUTE_FRAMES 选项将使得 ClassWriter 慢两倍
         */
        val cw = ClassWriter(0)
        //        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
//        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        var fv: FieldVisitor? = null
        var mv: MethodVisitor? = null
        var av0: AnnotationVisitor? = null
        objs["cw"] = cw
        objs["null"] = null
        val lines =
            Arrays.asList(*dumpJava.split("\n").toTypedArray())
        loop@ for (originLine in lines) {
            var line = originLine.trim { it <= ' ' }
            if (line.isEmpty()
                || line.startsWith("{")
                || line.startsWith("}")
                || line.startsWith("package")
                || line.startsWith("import")
                || line.startsWith("public")
                || line.startsWith("ClassWriter cw = new ClassWriter")
                || line.startsWith("ClassWriter classWriter = new ClassWriter")
                || line.startsWith("FieldVisitor fv;")
                || line.startsWith("FieldVisitor fieldVisitor;")
                || line.startsWith("MethodVisitor mv;")
                || line.startsWith("MethodVisitor methodVisitor;")
                || line.startsWith("AnnotationVisitor av0;")
                || line.startsWith("AnnotationVisitor annotationVisitor0;")
            ) {
                continue
            }
            val spList = splitLine2(line)
            var split = spList[0]
            var version: Int
            var name: String?
            var signature: String?
            var superName: String?
            var interfaces: Array<String>?
            var exceptions: Array<String>?
            var file: String?
            var debug: String?
            var desc: String?
            var owner: String?
            var type: String?
            var value: Any?
            var opcode: Int
            var operand: Int
            var nLocal: Int
            var nStack: Int
            var local: Array<Any?>?
            var stack: Array<Any?>?
            var itf: Boolean
            var handle: Handle
            var lIndex = -1
            var spIndex = -1
            var access = 0
            when (split!![0]) {
                "cw.visit", "classWriter.visit" -> {
                    version = getOpcodesVal(split[1])
                    access = getAccess(split[2])
                    name = getStringOrNull(split[3])
                    signature = getStringOrNull(split[4])
                    superName = getStringOrNull(split[5])
                    interfaces = toValue(spList, 0, 6) as Array<String>?
                    cw.visit(version, access, name, signature, superName, interfaces)
                }
                "cw.visitSource", "classWriter.visitSource" -> {
                    file = getStringOrNull(split[1])
                    debug = getStringOrNull(split[2])
                    cw.visitSource(file, debug)
                }
                "fv", "fieldVisitor" -> {
                    access = getAccess(split[2])
                    name = getStringOrNull(split[3])
                    desc = getStringOrNull(split[4])
                    signature = getStringOrNull(split[5])
                    value = toValue(spList, 0, 6)
                    fv = cw.visitField(access, name, desc, signature, value)
                }
                "fv.visitEnd", "fieldVisitor.visitEnd" -> fv!!.visitEnd()
                "mv", "methodVisitor" -> {
                    access = getAccess(split[2])
                    name = getStringOrNull(split[3])
                    desc = getStringOrNull(split[4])
                    signature = getStringOrNull(split[5])
                    exceptions = toValue(spList, 0, 6) as Array<String>?
                    mv = cw.visitMethod(access, name, desc, signature, exceptions)
                }
                "mv.visitCode", "methodVisitor.visitCode" -> mv!!.visitCode()
                "mv.visitLabel", "methodVisitor.visitLabel" -> mv!!.visitLabel(getLable(split[1]))
                "mv.visitLineNumber", "methodVisitor.visitLineNumber" -> mv!!.visitLineNumber(
                    toInt(
                        split[1]
                    ), getLable(split[2])
                )
                "mv.visitVarInsn", "methodVisitor.visitVarInsn" -> mv!!.visitVarInsn(
                    getOpcodesVal(
                        split[1]
                    ), toInt(split[2])
                )
                "mv.visitMethodInsn", "methodVisitor.visitMethodInsn" -> {
                    opcode = getOpcodesVal(split[1])
                    owner = getStringOrNull(split[2])
                    name = getStringOrNull(split[3])
                    desc = getStringOrNull(split[4])
                    itf = java.lang.Boolean.valueOf(split[5])
                    mv!!.visitMethodInsn(opcode, owner, name, desc, itf)
                }
                "mv.visitLdcInsn", "methodVisitor.visitLdcInsn" -> {
                    value = toValue(spList, 0, 1)
                    mv!!.visitLdcInsn(value)
                }
                "mv.visitFieldInsn", "methodVisitor.visitFieldInsn" -> {
                    opcode = getOpcodesVal(split[1])
                    owner = getStringOrNull(split[2])
                    name = getStringOrNull(split[3])
                    desc = getStringOrNull(split[4])
                    mv!!.visitFieldInsn(opcode, owner, name, desc)
                }
                "mv.visitInsn", "methodVisitor.visitInsn" -> {
                    opcode = getOpcodesVal(split[1])
                    mv!!.visitInsn(opcode)
                }
                "mv.visitTypeInsn", "methodVisitor.visitTypeInsn" -> {
                    opcode = getOpcodesVal(split[1])
                    type = getStringOrNull(split[2])
                    mv!!.visitTypeInsn(opcode, type)
                }
                "mv.visitInvokeDynamicInsn", "methodVisitor.visitInvokeDynamicInsn" -> {
                    name = getStringOrNull(split[1])
                    desc = getStringOrNull(split[2])
                    if (split[3] != "new Handle(Opcodes.H_INVOKESTATIC") {
                        throw RuntimeException("unkown visitInvokeDynamicInsn")
                    }
                    var sindex = -1
                    if (split[6]!!.endsWith("\")")) {
                        split[6] = split[6]!!.substring(0, split[6]!!.length - 1)
                        handle = Handle(
                            Opcodes.H_INVOKESTATIC,
                            getStringOrNull(split[4]),
                            getStringOrNull(split[5]),
                            getStringOrNull(split[6])
                        )
                        sindex = 7
                    } else if (split[7]!!.endsWith(")")) {
                        split[7] = split[7]!!.substring(0, split[7]!!.length - 1)
                        handle = Handle(
                            Opcodes.H_INVOKESTATIC,
                            getStringOrNull(split[4]),
                            getStringOrNull(split[5]),
                            getStringOrNull(split[6]),
                            java.lang.Boolean.parseBoolean(getStringOrNull(split[7]))
                        )
                        sindex = 8
                    } else {
                        throw RuntimeException("unkown visitInvokeDynamicInsn : " + split[1])
                    }
                    mv!!.visitInvokeDynamicInsn(
                        name,
                        desc,
                        handle,
                        *toValue(spList, 0, sindex) as Array<Any?>
                    )
                }
                "mv.visitMaxs", "methodVisitor.visitMaxs" -> mv!!.visitMaxs(
                    toInt(split[1]),
                    split[2]!!.toInt()
                )
                "mv.visitEnd", "methodVisitor.visitEnd" -> mv!!.visitEnd()
                "mv.visitIntInsn", "methodVisitor.visitIntInsn" -> {
                    operand = try {
                        toInt(split[2])
                    } catch (e: NumberFormatException) {
                        getOpcodesVal(split[2])
                    }
                    mv!!.visitIntInsn(getOpcodesVal(split[1]), operand)
                }
                "mv.visitFrame", "methodVisitor.visitFrame" -> {
                    opcode = getOpcodesVal(split[1])
                    nLocal = split[2]!!.toInt()
                    local = toValue(spList, 0, 3) as Array<Any?>?
                    if (local == null) {
                        lIndex = 0
                        spIndex = 4
                    } else {
                        spIndex = 0
                        lIndex = 2
                        split = spList[lIndex]
                    }
                    nStack = getStringOrNull(split!![spIndex])!!.toInt()
                    stack = toValue(spList, lIndex, spIndex + 1) as Array<Any?>?
                    mv!!.visitFrame(opcode, nLocal, local, nStack, stack)
                }
                "mv.visitTryCatchBlock", "methodVisitor.visitTryCatchBlock" -> mv!!.visitTryCatchBlock(
                    getLable(
                        split[1]
                    ),
                    getLable(split[2]),
                    getLable(split[3]),
                    getStringOrNull(split[4])
                )
                "mv.visitJumpInsn", "methodVisitor.visitJumpInsn" -> mv!!.visitJumpInsn(
                    getOpcodesVal(
                        split[1]
                    ), objs[split[2]] as Label?
                )
                "cw.visitEnd", "classWriter.visitEnd" -> cw.visitEnd()
                "mv.visitIincInsn", "methodVisitor.visitIincInsn" -> mv!!.visitIincInsn(
                    split[1]!!.toInt(),
                    split[2]!!.toInt()
                )
                "mv.visitLookupSwitchInsn", "methodVisitor.visitLookupSwitchInsn" -> {
                    val keys = toValue(spList, 0, 2) as IntArray?
                    if (keys == null) {
                        lIndex = 0
                        spIndex = 3
                    } else {
                        lIndex = 2
                        spIndex = 0
                    }
                    mv!!.visitLookupSwitchInsn(
                        getLable(split[1]),
                        keys,
                        toValue(spList, lIndex, spIndex) as Array<Label?>?
                    )
                }
                "mv.visitTableSwitchInsn", "methodVisitor.visitTableSwitchInsn" -> mv!!.visitTableSwitchInsn(
                    toInt(split[1]),
                    toInt(split[2]),
                    getLable(split[3]),
                    *toValue(spList, 0, 4) as Array<Label?>
                )
                "return cw.toByteArray", "return classWriter.toByteArray" -> {
                }
                "cw.visitInnerClass", "classWriter.visitInnerClass" -> {
                    name = getStringOrNull(split[1])
                    val outerName = getStringOrNull(split[2])
                    val innerName = getStringOrNull(split[3])
                    access = getAccess(split[4])
                    cw.visitInnerClass(name, outerName, innerName, access)
                }
                "av0", "annotationVisitor0" -> {
                    av0 = when (split[1]) {
                        "cw.visitAnnotation", "classWriter.visitAnnotation" -> cw.visitAnnotation(
                            getStringOrNull(split[2]),
                            java.lang.Boolean.valueOf(split[3])
                        )
                        "mv.visitAnnotation", "methodVisitor.visitAnnotation" -> mv!!.visitAnnotation(
                            getStringOrNull(split[2]),
                            java.lang.Boolean.valueOf(split[3])
                        )
                        "mv.visitAnnotationDefault", "methodVisitor.visitAnnotationDefault" -> mv!!.visitAnnotationDefault()
                        "mv.visitParameterAnnotation", "methodVisitor.visitParameterAnnotation" -> mv!!.visitParameterAnnotation(
                            split[2]!!.toInt(),
                            getStringOrNull(split[3]),
                            java.lang.Boolean.valueOf(getStringOrNull(split[4]))
                        )
                        else -> throw RuntimeException("unkown av0 : " + split[1])
                    }
                    objs["av0"] = av0
                    objs["annotationVisitor0"] = av0
                }
                "mv.visitMultiANewArrayInsn", "methodVisitor.visitMultiANewArrayInsn" -> mv!!.visitMultiANewArrayInsn(
                    getStringOrNull(split[1]),
                    split[2]!!.toInt()
                )
                "mv.visitAnnotableParameterCount", "methodVisitor.visitAnnotableParameterCount" -> mv!!.visitAnnotableParameterCount(
                    Integer.valueOf(getStringOrNull(split[1])),
                    java.lang.Boolean.valueOf(getStringOrNull(split[2]))
                )
                else -> {
                    var bok = true
                    var sp2 = split[0]!!.split("\\.").toTypedArray()
                    if (sp2[0].startsWith("av") || sp2[0].startsWith("annotationVisitor")) {
                        val av =
                            objs[sp2[0]] as AnnotationVisitor?
                        when (sp2[1]) {
                            "visit" -> av!!.visit(
                                getStringOrNull(split[1]),
                                toValue(spList, 0, 2)
                            )
                            "visitEnd" -> av!!.visitEnd()
                            "visitEnum" -> av!!.visitEnum(
                                getStringOrNull(split[1]),
                                getStringOrNull(split[2]),
                                getStringOrNull(split[3])
                            )
                            else -> bok = false
                        }
                    } else {
                        sp2 = split[0]!!.split("\\s+").toTypedArray()
                        when (sp2[0]) {
                            "Label" -> objs[sp2[1]] = Label()
                            "AnnotationVisitor" -> if (split[1] == "av0.visitArray" || split[1] == "annotationVisitor0.visitArray") {
                                val av =
                                    av0!!.visitArray(getStringOrNull(split[2]))
                                objs[sp2[1]] = av
                            } else if (split[1] == "av0.visitAnnotation" || split[1] == "annotationVisitor0.visitAnnotation") {
                                val av = av0!!.visitAnnotation(
                                    getStringOrNull(split[2]),
                                    getStringOrNull(split[3])
                                )
                                objs[sp2[1]] = av
                            } else {
                                throw RuntimeException("unkown : AnnotationVisitor " + split[0])
                            }
                            else -> bok = false
                        }
                    }
                    if (bok) {
                        break@loop
                    } else {
                        throw RuntimeException("unkown : " + split[0])
                    }
                }
            }
        }
        return cw.toByteArray()
    }

    @Throws(Throwable::class)
    @JvmStatic
    fun main(args: Array<String>) {
//        byte[] bytes = Files.readAllBytes(Paths.get("/Volumes/RsData/Projects/lotus/StringerRestore/Restore/dexprotector-ent-7.1.12/restore/dexprotector_AsmJava/com/licel/license/client/ActivationClient.java"));
//        String dumpJava = new String(bytes);
//        compile2class(dumpJava);
//        splitLine2("mv.visitLdcInsn(\"=\\\"\");");
//        splitLine2("mv.visitLdcInsn(\":nth-child(%d)\");");
//        splitLine2("mv.visitLdcInsn(\"(?i)\\\\bcharset=\\\\s*(?:\\\"|')?([^\\\\s,;\\\"']*)\");");
        splitLine2("mv.visitInvokeDynamicInsn(\"PqsFNfXN\", \"(Ljava/lang/Object;Z)V\", new Handle(Opcodes.H_INVOKESTATIC, \"com/licel/dexprotector/ab\", \"lq\", \"(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;\"), new Object[]{\"\\u9677\\u2b94\\u96a2\\u2bdc\\u96ac\\u2b9d\\u96ad\\u2bda\\u1593\\ua780\\u159c\\ua7cb\\u1594\\ua780\\u1588\\ua795\\u0ca6\\u95b6\\u0ca0\\u95bc\\u0cb7\\u95ad\\u0cbb\\u95ab\\u4c89\\u0009\\u4cce%\\u4cc2\\u0000\\u4cc4\\\"\\u2396\\u5423\\u2380\\u5400\\u2392\\u5422\\u2392\\u543d\\u3728\\u48b2\\u3728\\u48dd\\u372f\\u48fd\\u3735\\u48d9\\ud420\\uaae4\\ud427\\uaaec\\ud46a\\uaad2\\ud46b\\uaade\"});")
    }
}