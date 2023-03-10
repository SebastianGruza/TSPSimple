package com.tsp.solver.algorithm;

import com.tsp.solver.data.DistanceMatrix;

import java.util.ArrayList;
import java.util.List;

public class DFS implements Calculation {

    private int minDist = Integer.MAX_VALUE;
    private List<Integer> bestPath;
    private DistanceMatrix distanceMatrix;
    private int n;

    public DFS(int n, DistanceMatrix distanceMatrix, int[] path) {
        this.distanceMatrix = distanceMatrix;
        this.n = n;
        bestPath = new ArrayList<>();
    }

    public void calculation(List<Integer> DFSResults, List<int[]> bestPathDFS) {
        boolean[] visitedDFS = new boolean[n];
        List<Integer> pathDFS = new ArrayList<>();
        visitedDFS[0] = true;
        pathDFS.add(0);
        dfs(0, 0, n, distanceMatrix.getMatrix(), visitedDFS, pathDFS);
        DFSResults.add(minDist);
        bestPathDFS.add(pathDFS.stream().mapToInt(i->i).toArray());
    }

    private void dfs(int currPos, int currDist, int n, int[][] dist, boolean[] visited, List<Integer> path) {
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
