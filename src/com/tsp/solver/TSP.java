package com.tsp.solver;

import java.util.*;

class DistanceMatrix {

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

class TSP {
    private static int minDist = Integer.MAX_VALUE;
    private static List<Integer> bestPath = new ArrayList<>();
    private static List<Integer> pathDFS = new ArrayList<>();

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

    private static void greedyCalculate(int start, int n, List<Integer> greedyResults, DistanceMatrix distanceMatrix) {
        boolean[] visited = new boolean[n];
        int[] path = new int[n + 1];
        path[0] = start;
        visited[start] = true;
        int curr = start;

        // Algorytm zachłanny
        for (int i = 1; i < n; i++) {
            int next = findMin(curr, distanceMatrix.getMatrix(), visited, n);
            path[i] = next;
            visited[next] = true;
            curr = next;
        }

        // Powrót do miasta startowego
        path[n] = start;

        int total = 0;
        for (int i = 0; i < path.length - 1; i++) {
            total += distanceMatrix.getMatrix()[path[i]][path[i+1]];
        }
        greedyResults.add(total);
    }

    private static void DFSCalculate(int n, List<Integer> DFSResults, DistanceMatrix distanceMatrix) {
        boolean[] visitedDFS = new boolean[n];
        visitedDFS[0] = true;
        pathDFS.add(0);
        dfs(0, 0, n, distanceMatrix.getMatrix(), visitedDFS, pathDFS);
        DFSResults.add(minDist);
    }

    public static void main(String[] args) {
        calculateTSPAlgorithm(3, 13, true);
        calculateTSPAlgorithm(13, 100, false);
    }

    private static void calculateTSPAlgorithm(int start, int end, boolean isDFS) {
        int repeat = 10000;
        for (int n = start; n < end; n++) {
            List<Integer> greedyResultsSimple = new ArrayList<>();
            List<Integer> DFSResultsSimple = new ArrayList<>();
            List<DistanceMatrix> distances = generateDistances(n, repeat);
            long startTime = System.currentTimeMillis();
            for (int i = 0; i < repeat; i++) {
                greedyCalculate(0, n, greedyResultsSimple, distances.get(i));
            }
            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            System.out.println("Czas wykonywania greedySimple  dla n = " + n + " : " + totalTime + "ms");


            List<Integer> greedyResultsComplex = new ArrayList<>();
            startTime = System.currentTimeMillis();
            for (int i = 0; i < repeat; i++) {
                Integer best = Integer.MAX_VALUE / 2;
                for (int j = 0; j < n; j++) {
                    List<Integer> greedyResultsOneCity = new ArrayList<>();
                    greedyCalculate(j, n, greedyResultsOneCity, distances.get(i));
                    for (Integer result : greedyResultsOneCity) {
                        if (best > result) {
                            best = result;
                        }
                    }
                }
                greedyResultsComplex.add(best);
            }
            endTime = System.currentTimeMillis();
            totalTime = endTime - startTime;
            System.out.println("Czas wykonywania greedyComplex  dla n = " + n + " : " + totalTime + "ms");

            if (isDFS) {
                startTime = System.currentTimeMillis();
                for (int i = 0; i < repeat; i++) {
                    minDist = Integer.MAX_VALUE;
                    bestPath = new ArrayList<>();
                    pathDFS = new ArrayList<>();
                    DFSCalculate(n, DFSResultsSimple, distances.get(i));
                }
                endTime = System.currentTimeMillis();
                totalTime = endTime - startTime;
                System.out.println("Czas wykonywania            DFS   dla n = " + n + " : " + totalTime + "ms");
            }
            Double greedyMean = (1.0 * greedyResultsSimple.stream().reduce(0, Integer::sum) / repeat);
            Double greedyComplexMean = (1.0 * greedyResultsComplex.stream().reduce(0, Integer::sum) / repeat);
            System.out.println("n = " + n + ", średnia zachłannym = " + greedyMean);
            System.out.println("n = " + n + ", śr zach ze zm p st = " + greedyComplexMean);
            System.out.println("n = " + n + ",   stosunek greedyComplex/greedy = " + greedyComplexMean/greedyMean);
            if (isDFS) {
                Double DFSMean = (1.0 * DFSResultsSimple.stream().reduce(0, Integer::sum) / repeat);
                System.out.println("n = " + n + ", średnia       DFS  = " + DFSMean);
                System.out.println("n = " + n + ",   stosunek DFS/greedy        = " + DFSMean / greedyMean);
                System.out.println("n = " + n + ",   stosunek DFS/greedyComplex = " + DFSMean / greedyComplexMean);
            }

        }
    }

    static int findMin(int curr, int[][] dist, boolean[] visited, int n) {
        int min = Integer.MAX_VALUE;
        int next = -1;
        for (int i = 0; i < n; i++) {
            if (!visited[i] && dist[curr][i] < min) {
                min = dist[curr][i];
                next = i;
            }
        }
        return next;
    }

    private static void dfs(int currPos, int currDist, int n, int[][] dist, boolean[] visited, List<Integer> path) {
        if (currDist >= minDist) {
            return;
        }
        if (path.size() == n) {
            currDist += dist[currPos][0];
            if (currDist < minDist) {
                minDist = currDist;
                bestPath = new ArrayList<>(path);
                bestPath.add(0);
            }
            return;
        }
        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                visited[i] = true;
                path.add(i);
                dfs(i, currDist + dist[currPos][i], n, dist, visited, path);
                visited[i] = false;
                path.remove(path.size() - 1);
            }
        }
    }
}
