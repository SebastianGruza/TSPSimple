package com.tsp.solver;

import com.tsp.solver.algorithm.DFS;
import com.tsp.solver.algorithm.Greedy;
import com.tsp.solver.algorithm.TwoOpt;
import com.tsp.solver.data.DistanceMatrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class TSP {

    public static List<DistanceMatrix> generateDistances(int n, int repeat) {
        List<DistanceMatrix> distances = new ArrayList<>();
        Random r = new Random();
        for (int i = 0; i < repeat; i++) {
            // Generowanie symetrycznej macierzy odległości
            DistanceMatrix distanceMatrix = new DistanceMatrix(n);
            distanceMatrix.generate();
            distances.add(distanceMatrix);
        }
        return distances;
    }

    public static void main(String[] args) {
        calculateTSPAlgorithm(3, 14, true, 1);
        calculateTSPAlgorithm(14, 40, false, 1);
        calculateTSPAlgorithm(40, 80, false, 2);
        calculateTSPAlgorithm(80, 160, false, 4);
        calculateTSPAlgorithm(160, 320, false, 8);
    }

    private static void calculateTSPAlgorithm(int start, int end, boolean isDFS, int step) {
        int repeat = 1000;
        for (int n = start; n < end; n += step) {
            List<Integer> greedyResultsSimple = new ArrayList<>();
            List<Integer> DFSResultsSimple = new ArrayList<>();
            List<int[]> bestPathGreedy = new ArrayList<>();
            List<DistanceMatrix> distances = generateDistances(n, repeat);
            long startTime = System.currentTimeMillis();
            for (int i = 0; i < repeat; i++) {
                Greedy greedy = new Greedy(n, distances.get(i));
                greedy.greedyCalculate(0, greedyResultsSimple, bestPathGreedy);
            }
            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            System.out.println("Czas wykonywania greedySimple  dla n = " + n + " : " + totalTime + "ms");


            List<Integer> greedyResultsComplex = new ArrayList<>();
            List<int[]> bestPathGreedyComplex = new ArrayList<>();
            startTime = System.currentTimeMillis();
            for (int i = 0; i < repeat; i++) {
                Integer best = Integer.MAX_VALUE / 2;
                int[] bestPath = new int[n];
                for (int j = 0; j < n; j++) {
                    List<Integer> greedyResultsOneCity = new ArrayList<>();
                    List<int[]> bestPathGreedyOneCity = new ArrayList<>();
                    Greedy greedy = new Greedy(n, distances.get(i));
                    greedy.greedyCalculate(j, greedyResultsOneCity, bestPathGreedyOneCity);
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
            endTime = System.currentTimeMillis();
            totalTime = endTime - startTime;
            System.out.println("Czas wykonywania greedyComplex  dla n = " + n + " : " + totalTime + "ms");

            List<Integer> twoOptResults = new ArrayList<>();
            startTime = System.currentTimeMillis();
            for (int i = 0; i < repeat; i++) {
                TwoOpt twoOpt = new TwoOpt(distances.get(i).getMatrix(), bestPathGreedy.get(i));
                int[] path = twoOpt.twoOpt();
                int best = twoOpt.getPathDistance(path);
                twoOptResults.add(best);
            }
            endTime = System.currentTimeMillis();
            totalTime = endTime - startTime;
            System.out.println("Czas wykonywania twoOpt  dla n = " + n + " : " + totalTime + "ms");

            if (isDFS) {
                startTime = System.currentTimeMillis();
                for (int i = 0; i < repeat; i++) {
                    DFS dfs = new DFS(distances.get(i), n);
                    dfs.DFSCalculate(DFSResultsSimple);
                }
                endTime = System.currentTimeMillis();
                totalTime = endTime - startTime;
                System.out.println("Czas wykonywania            DFS   dla n = " + n + " : " + totalTime + "ms");
            }
            Double greedyMean = (1.0 * greedyResultsSimple.stream().reduce(0, Integer::sum) / repeat);
            Double greedyComplexMean = (1.0 * greedyResultsComplex.stream().reduce(0, Integer::sum) / repeat);
            Double twoOptMean = (1.0 * twoOptResults.stream().reduce(0, Integer::sum) / repeat);
            System.out.println("n = " + n + ", średnia zachłannym = " + greedyMean);
            System.out.println("n = " + n + ", śr zach ze zm p st = " + greedyComplexMean);
            System.out.println("n = " + n + ",    2-opt           = " + twoOptMean);
            System.out.println("n = " + n + ",   poprawa greedyComplex/greedy % = " + (1.0 - greedyComplexMean / greedyMean) * 100);
            System.out.println("n = " + n + ",   poprawa  2-opt/greedyComplex % = " + (1.0 - twoOptMean / greedyComplexMean) * 100);
            if (isDFS) {
                Double DFSMean = (1.0 * DFSResultsSimple.stream().reduce(0, Integer::sum) / repeat);
                System.out.println("n = " + n + ", średnia       DFS  = " + DFSMean);
                System.out.println("n = " + n + ",   poprawa DFS/greedy        % = " + (1.0 - DFSMean / greedyMean) * 100);
                System.out.println("n = " + n + ",   poprawa DFS/greedyComplex % = " + (1.0 - DFSMean / greedyComplexMean) * 100);
                System.out.println("n = " + n + ",   poprawa DFS/2-opt         % = " + (1.0 - DFSMean / twoOptMean) * 100);
            }
            System.out.println("---------------------------------------------");

        }
    }
}
