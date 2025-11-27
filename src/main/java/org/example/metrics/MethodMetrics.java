package org.example.metrics;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MethodMetrics {
    private String signature;

    private int assignments = 0;
    private int branches = 0;
    private int conditions = 0;

    private double abc = 0;
}
