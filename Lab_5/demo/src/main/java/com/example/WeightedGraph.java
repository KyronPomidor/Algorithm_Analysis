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

    // Undirected edge: added in both directions
    public void addEdge(int src, int dst, int weight) {
        LinkedList<Node> srcList = alist.get(src);
        LinkedList<Node> dstList = alist.get(dst);
        srcList.add(dstList.get(0));
        dstList.add(srcList.get(0));
        edges.get(src).add(new int[]{dst, weight});
        edges.get(dst).add(new int[]{src, weight});
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

    // -------------------------------------------------------------------------
    // Prim's Algorithm: greedy MST starting from vertex 0
    // Time complexity: O((V + E) log V) with a binary heap priority queue
    // -------------------------------------------------------------------------
    public int primMST() {
        int n = alist.size();
        int[] key = new int[n];
        boolean[] inMST = new boolean[n];
        Arrays.fill(key, Integer.MAX_VALUE);
        key[0] = 0;

        // PriorityQueue of {weight, nodeIndex}
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[0] - b[0]);
        pq.offer(new int[]{0, 0});

        int totalWeight = 0;
        while (!pq.isEmpty()) {
            int[] cur = pq.poll();
            int u = cur[1];
            if (inMST[u]) continue;
            inMST[u] = true;
            totalWeight += cur[0];

            for (int[] e : edges.get(u)) {
                int v = e[0], w = e[1];
                if (!inMST[v] && w < key[v]) {
                    key[v] = w;
                    pq.offer(new int[]{key[v], v});
                }
            }
        }
        return totalWeight;
    }

    // -------------------------------------------------------------------------
    // Kruskal's Algorithm: greedy MST by sorting all edges
    // Time complexity: O(E log E) dominated by edge sorting
    // -------------------------------------------------------------------------
    public int kruskalMST() {
        int n = alist.size();

        // Collect all unique edges (u < v to avoid duplicates since graph is undirected)
        List<int[]> allEdges = new ArrayList<>();
        for (int u = 0; u < n; u++) {
            for (int[] e : edges.get(u)) {
                if (u < e[0]) {
                    allEdges.add(new int[]{e[1], u, e[0]}); // {weight, src, dst}
                }
            }
        }

        // Sort edges by weight ascending
        allEdges.sort((a, b) -> a[0] - b[0]);

        // Union-Find (Disjoint Set Union) with path compression and union by rank
        int[] parent = new int[n];
        int[] rank   = new int[n];
        for (int i = 0; i < n; i++) parent[i] = i;

        int totalWeight = 0;
        for (int[] e : allEdges) {
            int w = e[0], u = e[1], v = e[2];
            int ru = find(parent, u);
            int rv = find(parent, v);
            if (ru != rv) {
                union(parent, rank, ru, rv);
                totalWeight += w;
            }
        }
        return totalWeight;
    }

    // Union-Find: find root with path compression
    private int find(int[] parent, int x) {
        if (parent[x] != x) parent[x] = find(parent, parent[x]);
        return parent[x];
    }

    // Union-Find: union by rank
    private void union(int[] parent, int[] rank, int rx, int ry) {
        if (rank[rx] < rank[ry]) { int t = rx; rx = ry; ry = t; }
        parent[ry] = rx;
        if (rank[rx] == rank[ry]) rank[rx]++;
    }
}