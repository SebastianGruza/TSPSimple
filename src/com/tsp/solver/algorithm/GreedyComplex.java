package com.tsp.solver.algorithm;

import com.tsp.solver.data.DistanceMatrix;

import java.util.ArrayList;
import java.util.List;

public class GreedyComplex implements  Calculation {

    private int n;
    private DistanceMatrix distanceMatrix;

    public GreedyComplex(int n, DistanceMatrix distanceMatrix, int[] path, int start) {
        this.n = n;
        this.distanceMatrix = distanceMatrix;
    }

    public void calculation(List<Integer> greedyResultsComplex, List<int[]> bestPathGreedyComplex) {
        Integer best = Integer.MAX_VALUE / 2;
        int[] bestPath = new int[n];
        for (int j = 0; j < n; j++) {
            List<Integer> greedyResultsOneCity = new ArrayList<>();
            List<int[]> bestPathGreedyOneCity = new ArrayList<>();
            Greedy greedy = new Greedy(n, distanceMatrix, null, j);
            greedy.calculation(greedyResultsOneCity, bestPathGreedyOneCity);
            for (Integer k = 0; k < greedyResultsOneCity.size(); k++) {
                Integer result = greedyResultsOneCity.get(k);
                if (best > result) {
                    best = result;
                    bestPath = bestPathGreedyOneCity.get(k);
                }
            }
        }
        greedyResultsComplex.add(best);
        bestPathGreedyComplex.add(bestPath);
    }
}
