package com.tsp.solver;

import com.tsp.solver.algorithm.*;
import com.tsp.solver.data.DistanceMatrix;

import java.util.ArrayList;
import java.util.List;

class TSP {

    public static List<DistanceMatrix> generateDistances(int n, int repeat) {
        List<DistanceMatrix> distances = new ArrayList<>();
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

    private static void process(Calculation[] algorithm, int repeat, int n, List<Integer> greedyResultsSimple, List<int[]> bestPathGreedy) {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < repeat; i++) {
            algorithm[i].calculation(greedyResultsSimple, bestPathGreedy);
        }
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Czas wykonywania " + algorithm.getClass().getName() + " dla n = " + n + " : " + totalTime + "ms");
    }

    private static void calculateTSPAlgorithm(int start, int end, boolean isDFS, int step) {
        int repeat = 1000;
        for (int n = start; n < end; n += step) {
            // Przygotowanie danych:
            List<DistanceMatrix> distances = generateDistances(n, repeat);
            List<List<Integer>> results = new ArrayList<>();
            List<List<int[]>> paths = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                results.add(new ArrayList<>());
                paths.add(new ArrayList<>());
            }

            // Algorytm zachłanny:
            Greedy[] greedy = new Greedy[repeat];
            for (int i = 0; i < repeat; i++) {
                greedy[i] = new Greedy(n, distances.get(i), null, 0);
            }
            process(greedy, repeat, n, results.get(0), paths.get(0));

            // Algorytm zachłanny ze zmiennym startem:
            GreedyComplex[] greedyComplex = new GreedyComplex[repeat];
            for (int i = 0; i < repeat; i++) {
                greedyComplex[i] = new GreedyComplex(n, distances.get(i), null, 0);
            }
            process(greedyComplex, repeat, n, results.get(1), paths.get(1));

            // Algorytm 2-opt, korzystający z wyniku algorytmu zachłannego ze zmiennym startem:
            TwoOpt[] twoOpt = new TwoOpt[repeat];
            for (int i = 0; i < repeat; i++) {
                twoOpt[i] = new TwoOpt(n, distances.get(i), paths.get(1).get(i), 0);
            }
            process(twoOpt, repeat, n, results.get(2), paths.get(2));

            // Algorytm DFS, tylko dla małych n:
            if (isDFS) {
                DFS[] dfs = new DFS[repeat];
                for (int i = 0; i < repeat; i++) {
                    dfs[i] = new DFS(n, distances.get(i), null);
                }
                process(dfs, repeat, n, results.get(3), paths.get(3));

            }

            // Drukowanie statystyk:
            Double greedyMean = (1.0 * results.get(0).stream().reduce(0, Integer::sum) / repeat);
            Double greedyComplexMean = (1.0 * results.get(1).stream().reduce(0, Integer::sum) / repeat);
            Double twoOptMean = (1.0 * results.get(2).stream().reduce(0, Integer::sum) / repeat);
            System.out.println("n = " + n + ", średnia zachłannym = " + greedyMean);
            System.out.println("n = " + n + ", śr zach ze zm p st = " + greedyComplexMean);
            System.out.println("n = " + n + ",    2-opt           = " + twoOptMean);
            System.out.println("n = " + n + ",   poprawa greedyComplex/greedy % = " + (1.0 - greedyComplexMean / greedyMean) * 100);
            System.out.println("n = " + n + ",   poprawa  2-opt/greedyComplex % = " + (1.0 - twoOptMean / greedyComplexMean) * 100);
            if (isDFS) {
                Double DFSMean = (1.0 * results.get(3).stream().reduce(0, Integer::sum) / repeat);
                System.out.println("n = " + n + ", średnia       DFS  = " + DFSMean);
                System.out.println("n = " + n + ",   poprawa DFS/greedy        % = " + (1.0 - DFSMean / greedyMean) * 100);
                System.out.println("n = " + n + ",   poprawa DFS/greedyComplex % = " + (1.0 - DFSMean / greedyComplexMean) * 100);
                System.out.println("n = " + n + ",   poprawa DFS/2-opt         % = " + (1.0 - DFSMean / twoOptMean) * 100);
            }
            System.out.println("---------------------------------------------");

        }
    }
}
