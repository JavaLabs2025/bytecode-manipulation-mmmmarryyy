package org.example.metrics;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class ClassMetrics {
    private String className;
    private String superClassName;
    private int fieldCount = 0;
    private List<MethodMetrics> methodsMetrics = new ArrayList<>();

    public void addMethodMetrics(MethodMetrics methodMetrics) {
        methodsMetrics.add(methodMetrics);
    }
}
