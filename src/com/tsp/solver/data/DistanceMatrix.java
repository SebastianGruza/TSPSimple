package com.tsp.solver.data;

import java.util.Random;

public class DistanceMatrix {

    private int[][] matrix;
    private int n;
    private Random random = new Random();

    public DistanceMatrix(int n) {
        this.n = n;
        matrix = new int[n][n];
    }

    public void generate() {
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int distance = random.nextInt(100) + 1;
                matrix[i][j] = distance;
                matrix[j][i] = distance;
            }
        }
    }

    public int[][] getMatrix() {
        return matrix;
    }
}
