package com.example;

import java.util.*;

public class WeightedGraph {

    ArrayList<LinkedList<Node>> alist;
    ArrayList<LinkedList<int[]>> edges; // each int[] is {neighborIndex, weight}

    public WeightedGraph() {
        alist = new ArrayList<>();
        edges = new ArrayList<>();
    }

    public void addNode(Node node) {
        LinkedList<Node> currentList = new LinkedList<>();
        currentList.add(node);
        alist.add(currentList);
        edges.add(new LinkedList<>());
    }

    public void addEdge(int src, int dst, int weight) {
        LinkedList<Node> currentList = alist.get(src);
        Node dstNode = alist.get(dst).get(0);
        currentList.add(dstNode);
        edges.get(src).add(new int[]{dst, weight});
    }

    public boolean checkEdge(int src, int dst) {
        for (int[] e : edges.get(src)) {
            if (e[0] == dst) return true;
        }
        return false;
    }

    public int size() {
        return alist.size();
    }

    // Returns adjacency matrix with 0 meaning no edge (used by Floyd-Warshall)
    public int[][] toMatrix() {
        int n = alist.size();
        int[][] mat = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int[] e : edges.get(i)) {
                mat[i][e[0]] = e[1];
            }
        }
        return mat;
    }

    // Dijkstra: single-source shortest path from src
    public int[] dijkstra(int src) {
        int n = alist.size();
        int[] dist = new int[n];
        boolean[] visited = new boolean[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[src] = 0;

        // PriorityQueue of {distance, nodeIndex}
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[0] - b[0]);
        pq.offer(new int[]{0, src});

        while (!pq.isEmpty()) {
            int[] cur = pq.poll();
            int u = cur[1];
            if (visited[u]) continue;
            visited[u] = true;

            for (int[] e : edges.get(u)) {
                int v = e[0], w = e[1];
                if (!visited[v] && dist[u] + w < dist[v]) {
                    dist[v] = dist[u] + w;
                    pq.offer(new int[]{dist[v], v});
                }
            }
        }
        return dist;
    }

    // Floyd-Warshall: all-pairs shortest paths
    public int[][] floydWarshall() {
        int n = alist.size();
        int INF = Integer.MAX_VALUE / 2;
        int[][] dist = new int[n][n];

        // Initialize distances
        for (int i = 0; i < n; i++) {
            Arrays.fill(dist[i], INF);
            dist[i][i] = 0;
        }
        for (int i = 0; i < n; i++) {
            for (int[] e : edges.get(i)) {
                dist[i][e[0]] = e[1];
            }
        }

        // Relax through every intermediate vertex k
        for (int k = 0; k < n; k++)
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++)
                    if (dist[i][k] + dist[k][j] < dist[i][j])
                        dist[i][j] = dist[i][k] + dist[k][j];

        return dist;
    }
}