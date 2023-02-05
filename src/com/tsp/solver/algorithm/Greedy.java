package com.tsp.solver.algorithm;

import com.tsp.solver.data.DistanceMatrix;

import java.util.Arrays;
import java.util.List;

public class Greedy {

    private int n;
    private DistanceMatrix distanceMatrix;

    public Greedy(int n, DistanceMatrix distanceMatrix) {
        this.n = n;
        this.distanceMatrix = distanceMatrix;
    }

    private static Integer bestPathGreedyDistances = Integer.MAX_VALUE;

    public void greedyCalculate(int start, List<Integer> greedyResults, List<int[]> bestPathGreedy) {
        boolean[] visited = new boolean[n];
        int[] path = new int[n + 1];
        int[] bestPath = new int[n];
        bestPathGreedyDistances = Integer.MAX_VALUE;
        path[0] = start;
        visited[start] = true;
        int curr = start;

        // Algorytm zachłanny
        for (int i = 1; i < n; i++) {
            int next = findMin(curr, visited);
            path[i] = next;
            visited[next] = true;
            curr = next;
        }

        // Powrót do miasta startowego
        path[n] = start;

        int total = 0;
        for (int i = 0; i < path.length - 1; i++) {
            total += distanceMatrix.getMatrix()[path[i]][path[i + 1]];
        }
        if (total < bestPathGreedyDistances) {
            bestPathGreedyDistances = total;
            bestPath = Arrays.copyOf(path, path.length - 1);
        }
        greedyResults.add(total);
        bestPathGreedy.add(bestPath);
    }

    int findMin(int curr, boolean[] visited) {
        int min = Integer.MAX_VALUE;
        int next = -1;
        for (int i = 0; i < n; i++) {
            if (!visited[i] && distanceMatrix.getMatrix()[curr][i] < min) {
                min = distanceMatrix.getMatrix()[curr][i];
                next = i;
            }
        }
        return next;
    }
}
