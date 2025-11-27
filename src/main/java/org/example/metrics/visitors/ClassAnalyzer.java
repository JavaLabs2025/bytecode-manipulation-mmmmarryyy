package org.example.metrics.visitors;

import org.example.metrics.ClassMetrics;
import org.example.metrics.GlobalMetrics;
import org.example.metrics.MethodMetrics;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ClassAnalyzer extends ClassVisitor {
    GlobalMetrics globalMetrics;
    ClassMetrics classMetrics = new ClassMetrics();

    public ClassAnalyzer(GlobalMetrics globalMetrics) {
        super(Opcodes.ASM9);
        this.globalMetrics = globalMetrics;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        classMetrics.setClassName(name);
        classMetrics.setSuperClassName(superName);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        classMetrics.setFieldCount(classMetrics.getFieldCount() + 1);
        return super.visitField(access, name, descriptor, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        if (!name.equals("<init>") && !name.equals("<clinit>")) {
            MethodMetrics methodMetrics = new MethodMetrics();
            methodMetrics.setSignature(signature);
            classMetrics.addMethodMetrics(methodMetrics);
            return new MethodAnalyzer(methodMetrics);
        }

        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }

    @Override
    public void visitEnd() {
        globalMetrics.addClassHierarchy(classMetrics.getClassName(), classMetrics.getSuperClassName());
        globalMetrics.addClassMethods(
                classMetrics.getClassName(),
                classMetrics.getMethodsMetrics().stream().map(MethodMetrics::getSignature).toList()
        );
        globalMetrics.addClassMetrics(classMetrics);

        super.visitEnd();
    }
}
