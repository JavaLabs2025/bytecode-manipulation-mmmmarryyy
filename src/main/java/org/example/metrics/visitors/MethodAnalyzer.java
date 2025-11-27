package org.example.metrics.visitors;

import org.example.metrics.MethodMetrics;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Set;

public class MethodAnalyzer extends MethodVisitor {
    private static final Set<Integer> CONDITIONAL_JUMPS = Set.of(
            Opcodes.IFEQ, Opcodes.IFNE, Opcodes.IFLT, Opcodes.IFGE, Opcodes.IFGT, Opcodes.IFLE,
            Opcodes.IF_ICMPEQ, Opcodes.IF_ICMPNE, Opcodes.IF_ICMPGE, Opcodes.IF_ICMPGT, Opcodes.IF_ICMPLE,
            Opcodes.IF_ACMPEQ, Opcodes.IF_ACMPNE, Opcodes.IFNULL, Opcodes.IFNONNULL
    );
    MethodMetrics methodMetrics;

    public MethodAnalyzer(MethodMetrics methodMetrics) {
        super(Opcodes.ASM9);
        this.methodMetrics = methodMetrics;
    }

    @Override
    public void visitVarInsn(int opcode, int var) {
        if (
                opcode == Opcodes.ISTORE ||
                        opcode == Opcodes.LSTORE ||
                        opcode == Opcodes.FSTORE ||
                        opcode == Opcodes.DSTORE ||
                        opcode == Opcodes.ASTORE
        ) {
            methodMetrics.setAssignments(methodMetrics.getAssignments() + 1);
        }
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        methodMetrics.setBranches(methodMetrics.getBranches() + 1);
        if (CONDITIONAL_JUMPS.contains(opcode)) {
            methodMetrics.setConditions(methodMetrics.getConditions() + 1);
        }
    }

    @Override
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        methodMetrics.setBranches(methodMetrics.getBranches() + 1);
        methodMetrics.setConditions(methodMetrics.getConditions() + labels.length + 1);
    }


    @Override
    public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
        methodMetrics.setBranches(methodMetrics.getBranches() + 1);
        methodMetrics.setConditions(methodMetrics.getConditions() + labels.length + 1);
    }

    @Override
    public void visitEnd() {
        int a = methodMetrics.getAssignments();
        int b = methodMetrics.getBranches();
        int c = methodMetrics.getConditions();
        methodMetrics.setAbc(Math.sqrt(a*a + b*b + c*c));
        super.visitEnd();
    }
}
