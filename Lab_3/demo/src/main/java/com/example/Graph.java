package com.example;
import java.util.*;

public class Graph {

    ArrayList<LinkedList<Node>> alist;
    ArrayList<LinkedList<Integer>> edges;  // stores neighbor indices directly

    public Graph() {
        alist = new ArrayList<>();
        edges = new ArrayList<>();
    }

    public void addNode(Node node) {
        LinkedList<Node> currentList = new LinkedList<>();
        currentList.add(node);
        alist.add(currentList);
        edges.add(new LinkedList<>());  // empty neighbor index list for this node
    }

    public void addEdge(int src, int dst) {
        LinkedList<Node> currentList = alist.get(src);
        Node dstNode = alist.get(dst).get(0);
        currentList.add(dstNode);
        edges.get(src).add(dst);  // store index directly
    }

    public boolean checkNode(int src, int dst) {
        LinkedList<Node> currentList = alist.get(src);
        Node dstNode = alist.get(dst).get(0);

        for (Node node : currentList) {
            if (node == dstNode)
                return true;
        }

        return false;
    }

    public void print() {
        for (LinkedList<Node> currentList : alist) {
            for (Node node : currentList) {
                System.out.print(node.data + "-> ");
            }
            System.out.println();
        }
    }

    private void dfsHelper(int src, boolean[] visited) {
        if (visited[src]) {
            return;
        } else {
            visited[src] = true;
        }

        for (int neighbor : edges.get(src)) {
            if (!visited[neighbor])
                dfsHelper(neighbor, visited);
        }
    }

    public void DepthFirstSearch(int src) {
        boolean[] visited = new boolean[alist.size()];
        dfsHelper(src, visited);
    }

    public void BreadthFirstSearch(int src) {
        Queue<Integer> queue = new LinkedList<>();
        boolean[] visited = new boolean[alist.size()];

        queue.add(src);
        visited[src] = true;

        while (queue.size() != 0) {
            int current = queue.poll();

            for (int neighbor : edges.get(current)) {
                if (!visited[neighbor]) {
                    queue.add(neighbor);
                    visited[neighbor] = true;
                }
            }
        }
    }

}