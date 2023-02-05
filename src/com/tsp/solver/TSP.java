package com.tsp.solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

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
    private static Integer bestPathGreedyDistances = Integer.MAX_VALUE;
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

    private static void greedyCalculate(int start, int n, List<Integer> greedyResults, List<int[]> bestPathGreedy, DistanceMatrix distanceMatrix) {
        boolean[] visited = new boolean[n];
        int[] path = new int[n + 1];
        int[] bestPath = new int[n];
        bestPathGreedyDistances = Integer.MAX_VALUE;
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
            total += distanceMatrix.getMatrix()[path[i]][path[i + 1]];
        }
        if (total < bestPathGreedyDistances) {
            bestPathGreedyDistances = total;
            bestPath = Arrays.copyOf(path, path.length - 1);
        }
        greedyResults.add(total);
        bestPathGreedy.add(bestPath);
    }

    private static void DFSCalculate(int n, List<Integer> DFSResults, DistanceMatrix distanceMatrix) {
        boolean[] visitedDFS = new boolean[n];
        visitedDFS[0] = true;
        pathDFS.add(0);
        dfs(0, 0, n, distanceMatrix.getMatrix(), visitedDFS, pathDFS);
        DFSResults.add(minDist);
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
                greedyCalculate(0, n, greedyResultsSimple, bestPathGreedy, distances.get(i));
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
                    greedyCalculate(j, n, greedyResultsOneCity, bestPathGreedyOneCity, distances.get(i));
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
                int[] path = twoOpt(bestPathGreedy.get(i), distances.get(i).getMatrix());
                int best = getPathDistance(path, distances.get(i).getMatrix());
                twoOptResults.add(best);
            }
            endTime = System.currentTimeMillis();
            totalTime = endTime - startTime;
            System.out.println("Czas wykonywania twoOpt  dla n = " + n + " : " + totalTime + "ms");

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

    public static int[] twoOpt(int[] path, int[][] dist) {
        int n = path.length;
        int[] newPath = new int[n];
        System.arraycopy(path, 0, newPath, 0, n);
        int bestDist = getPathDistance(newPath, dist);

        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                int[] tempPath = swap(newPath, i, j);
                int tempDist = getPathDistance(tempPath, dist);
                if (tempDist < bestDist) {
                    System.arraycopy(tempPath, 0, newPath, 0, n);
                    bestDist = tempDist;
                    i = -1;
                    break;
                }
            }
        }

        return newPath;
    }

    public static int[] swap(int[] path, int i, int j) {
        int n = path.length;
        int[] newPath = new int[n];
        System.arraycopy(path, 0, newPath, 0, i);
        int k = i;
        for (int m = j; m >= i; m--) {
            newPath[k] = path[m];
            k++;
        }
        System.arraycopy(path, j + 1, newPath, k, n - j - 1);
        return newPath;
    }

    public static int getPathDistance(int[] path, int[][] dist) {
        int n = path.length;
        int totalDist = 0;
        for (int i = 0; i < n - 1; i++) {
            totalDist += dist[path[i]][path[i + 1]];
        }
        totalDist += dist[path[n - 1]][path[0]];
        return totalDist;
    }

}
