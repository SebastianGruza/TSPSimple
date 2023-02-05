package com.tsp.solver;

import java.util.Arrays;

public class Main {
    static int[] path;
    static int total = 0;

    static void DFS(int u, int n) {
        if (u == n - 1) {
            total++;
        } else {
            for (int v = 0; v < n - 1; v++) {
                if (path[v] == -1) {
                    path[v] = u;
                    DFS(u + 1, n);
                    path[v] = -1;
                }
            }
        }
    }

    public static void main(String[] args) {
        for (int n = 3; n < 14; n++) {
            path = new int[n];
            Arrays.fill(path, -1);
            DFS(0, n);
            System.out.println("Możliwych tras komiwojażera dla n = " + n + " : " +  total / 2);
            total = 0;
        }
    }
}


