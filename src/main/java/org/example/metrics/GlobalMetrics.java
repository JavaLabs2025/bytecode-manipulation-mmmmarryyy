package org.example.metrics;


import java.util.*;

public class GlobalMetrics {
    Map<String, String> parents = new HashMap<>();
    Map<String, List<String>> methodSignatures = new HashMap<>();
    Map<String, Integer> depths = new HashMap<>();
    Map<String, Integer> overriddenMethodsCounter = new HashMap<>();
    Map<String, ClassMetrics> classMetricsMap = new HashMap<>();

    public void addClassHierarchy(String className, String superClassName) {
        parents.put(className, superClassName);
    }

    public void addClassMethods(String className, List<String> methodSignatures) {
        this.methodSignatures.put(className, methodSignatures);
    }

    public void addClassMetrics(ClassMetrics classMetrics) {
        this.classMetricsMap.put(classMetrics.getClassName(), classMetrics);
    }

    private void calculateInheritanceDepth(String className) {
        int depth = 0;
        String currentClassName = className;

        while (parents.containsKey(currentClassName)) {
            depth++;
            currentClassName = parents.get(currentClassName);
        }

        depths.put(className, depth);
    }

    private void countOverriddenMethods(String className, List<String> methods) {
        if (!parents.containsKey(className)) {
            overriddenMethodsCounter.put(className, 0);
            return;
        }

        String parentClassName = parents.get(className);
        Set<String> allParentMethods = new HashSet<>();

        while (parentClassName != null && parents.containsKey(parentClassName)) {
            if (methodSignatures.containsKey(parentClassName)) {
                allParentMethods.addAll(methodSignatures.get(parentClassName));
            }

            parentClassName = parents.get(parentClassName);
        }

        int counter = 0;

        for (String method : methods) {
            if (allParentMethods.contains(method)) {
                counter++;
            }
        }

        overriddenMethodsCounter.put(className, counter);
    }

    public void calculateFinalMetrics() {
        for (String className : parents.keySet()) {
            calculateInheritanceDepth(className);
        }

        for (String className : methodSignatures.keySet()) {
            List<String> methods = methodSignatures.get(className);
            countOverriddenMethods(className, methods);
        }
    }

    public int getMaxInheritanceDepth() {
        return depths.values().stream().max(Integer::compareTo).orElse(0);
    }

    public double getAvgInheritanceDepth() {
        return depths.values().stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);
    }

    public double getAvgAbcMetric() {
        return classMetricsMap.values().stream()
                .flatMap(classMetrics -> classMetrics.getMethodsMetrics().stream())
                .mapToDouble(MethodMetrics::getAbc)
                .average()
                .orElse(0.0);
    }

    public double getAvgOverriddenMethods() {
        return overriddenMethodsCounter.values().stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);
    }

    public double getAvgFieldCount() {
        return classMetricsMap.values().stream()
                .mapToInt(ClassMetrics::getFieldCount)
                .average()
                .orElse(0.0);
    }
}
