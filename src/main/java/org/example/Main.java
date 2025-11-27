package org.example;

import org.example.metrics.GlobalMetrics;
import org.example.metrics.visitors.ClassAnalyzer;
import org.objectweb.asm.ClassReader;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Main {
    public static void main(String[] args) throws IOException {
        String inputJar = "src/main/resources/guava.jar";
        String outputFile = "src/main/resources/output.json";

        if (args.length == 2) {
            inputJar = args[0];
            outputFile = args[1];
        }

        GlobalMetrics globalMetrics = new GlobalMetrics();

        try (JarFile inputJarFile = new JarFile(inputJar)) {
            Enumeration<JarEntry> enumeration = inputJarFile.entries();

            while (enumeration.hasMoreElements()) {
                JarEntry entry = enumeration.nextElement();
                if (entry.getName().endsWith(".class")) {
                    ClassReader reader = new ClassReader(inputJarFile.getInputStream(entry));
                    ClassAnalyzer analyzer = new ClassAnalyzer(globalMetrics);
                    reader.accept(analyzer, 0);
                }
            }
        }

        globalMetrics.calculateFinalMetrics();

        printMetricsToConsole(globalMetrics);

        saveMetricsToJson(globalMetrics, outputFile);
    }

    private static void printMetricsToConsole(GlobalMetrics metrics) {
        System.out.println("Максимальная глубина наследования: " + metrics.getMaxInheritanceDepth());
        System.out.println("Средняя глубина наследования: " + metrics.getAvgInheritanceDepth());
        System.out.println("Средняя метрика ABC: " + metrics.getAvgAbcMetric());
        System.out.println("Среднее количество переопределенных методов: " + metrics.getAvgOverriddenMethods());
        System.out.println("Среднее количество полей в классе: " + metrics.getAvgFieldCount());
    }

    private static void saveMetricsToJson(GlobalMetrics metrics, String outputFile) {
        String json = String.format(
                "{\n" +
                        "  \"maxInheritanceDepth\": %d,\n" +
                        "  \"avgInheritanceDepth\": %f,\n" +
                        "  \"avgAbcMetric\": %f,\n" +
                        "  \"avgOverriddenMethods\": %f,\n" +
                        "  \"avgFieldCount\": %f\n" +
                        "}",
                metrics.getMaxInheritanceDepth(),
                metrics.getAvgInheritanceDepth(),
                metrics.getAvgAbcMetric(),
                metrics.getAvgOverriddenMethods(),
                metrics.getAvgFieldCount()
        );

        try (FileWriter writer = new FileWriter(outputFile)) {
            writer.write(json);
            System.out.println("Метрики сохранены в файл: " + outputFile);
        } catch (IOException e) {
            System.err.println("Ошибка при записи в файл: " + e.getMessage());
        }
    }
}
