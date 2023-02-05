package com.tsp.solver.algorithm;

public class TwoOpt {

    int[][] dist;
    int[] path;

    public TwoOpt(int[][] dist, int[] path) {
        this.dist = dist;
        this.path = path;
    }

    public int[] twoOpt() {
        int n = path.length;
        int[] newPath = new int[n];
        System.arraycopy(path, 0, newPath, 0, n);
        int bestDist = getPathDistance(newPath);

        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                int[] tempPath = swap(newPath, i, j);
                int tempDist = getPathDistance(tempPath);
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

    public int[] swap(int[] path, int i, int j) {
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

    public int getPathDistance(int[] path) {
        int n = path.length;
        int totalDist = 0;
        for (int i = 0; i < n - 1; i++) {
            totalDist += dist[path[i]][path[i + 1]];
        }
        totalDist += dist[path[n - 1]][path[0]];
        return totalDist;
    }
}
