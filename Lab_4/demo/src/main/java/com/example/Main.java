package com.example;

import java.util.Random;
import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class Main {

    static final int MAX_WEIGHT = 100;

    // Sparse graph: ~2 edges per node (like a road network)
    static WeightedGraph generateSparseGraph(int n) {
        WeightedGraph graph = new WeightedGraph();
        Random rnd = new Random();

        for (int i = 0; i < n; i++)
            graph.addNode(new Node((char) ('A' + (i % 26))));

        // Chain so the graph is always connected
        for (int i = 0; i < n - 1; i++)
            graph.addEdge(i, i + 1, rnd.nextInt(MAX_WEIGHT) + 1);

        // Add a few extra random edges (sparse: ~2n total edges)
        int extra = n * 2;
        for (int i = 0; i < extra; i++) {
            int src = rnd.nextInt(n);
            int dst = rnd.nextInt(n);
            if (src != dst && !graph.checkEdge(src, dst))
                graph.addEdge(src, dst, rnd.nextInt(MAX_WEIGHT) + 1);
        }

        return graph;
    }

    // Dense graph: ~n*(n-1)/2 edges (nearly fully connected)
    static WeightedGraph generateDenseGraph(int n) {
        WeightedGraph graph = new WeightedGraph();
        Random rnd = new Random();

        for (int i = 0; i < n; i++)
            graph.addNode(new Node((char) ('A' + (i % 26))));

        // Connect every pair of nodes
        for (int i = 0; i < n; i++)
            for (int j = i + 1; j < n; j++)
                graph.addEdge(i, j, rnd.nextInt(MAX_WEIGHT) + 1);

        return graph;
    }

    static double measureDijkstra(WeightedGraph graph) {
        int runs = 5;
        long total = 0;
        for (int i = 0; i < runs; i++) {
            long t0 = System.nanoTime();
            graph.dijkstra(0);
            total += System.nanoTime() - t0;
        }
        return (total / runs) / 1_000_000d;
    }

    static double measureFloydWarshall(WeightedGraph graph) {
        int runs = 5;
        long total = 0;
        for (int i = 0; i < runs; i++) {
            long t0 = System.nanoTime();
            graph.floydWarshall();
            total += System.nanoTime() - t0;
        }
        return (total / runs) / 1_000_000d;
    }

    static void generateImage(XYSeries sparseSeries, XYSeries denseSeries, String algorithmName) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(sparseSeries);
        dataset.addSeries(denseSeries);

        JFreeChart chart = ChartFactory.createXYLineChart(
                algorithmName + " Execution Time vs Graph Size",
                "Number of nodes",
                "Time (ms)",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        chart.setBackgroundPaint(Color.WHITE);

        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setRangeGridlinePaint(new Color(220, 220, 220));
        plot.setDomainGridlinePaint(new Color(220, 220, 220));

        XYLineAndShapeRenderer r = new XYLineAndShapeRenderer();
        // Sparse = blue, Dense = orange
        r.setSeriesPaint(0, new Color(0x37, 0x8A, 0xDD));
        r.setSeriesPaint(1, new Color(0xFF, 0x7F, 0x0E));
        r.setSeriesStroke(0, new BasicStroke(2.0f));
        r.setSeriesStroke(1, new BasicStroke(2.0f));
        r.setSeriesShapesVisible(0, true);
        r.setSeriesShapesVisible(1, true);
        plot.setRenderer(r);

        chart.getTitle().setFont(new Font("SansSerif", Font.BOLD, 14));
        plot.getDomainAxis().setTickLabelFont(new Font("SansSerif", Font.PLAIN, 11));
        plot.getRangeAxis().setTickLabelFont(new Font("SansSerif", Font.PLAIN, 11));

        try {
            BufferedImage img = chart.createBufferedImage(700, 420);
            ImageIO.write(img, "png", new File(algorithmName + "_chart.png"));
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        int[] sizes = { 10, 50, 100, 200, 300, 400, 500, 1000 };

        XYSeries dijkstraSparse = new XYSeries("Dijkstra - Sparse");
        XYSeries dijkstraDense  = new XYSeries("Dijkstra - Dense");
        XYSeries floydSparse    = new XYSeries("Floyd-Warshall - Sparse");
        XYSeries floydDense     = new XYSeries("Floyd-Warshall - Dense");

        System.out.println("Dijkstra:\n");
        System.out.printf("%-10s  %-15s  %-15s%n", "Nodes", "Sparse (ms)", "Dense (ms)");
        System.out.println("-".repeat(44));

        for (int n : sizes) {
            WeightedGraph sparse = generateSparseGraph(n);
            WeightedGraph dense  = generateDenseGraph(n);

            double msSparse = measureDijkstra(sparse);
            double msDense  = measureDijkstra(dense);

            dijkstraSparse.add(n, msSparse);
            dijkstraDense.add(n, msDense);

            System.out.printf("n = %5d  ->  sparse: %.3f ms   dense: %.3f ms%n", n, msSparse, msDense);
        }

        System.out.println("\nFloyd-Warshall:\n");
        System.out.printf("%-10s  %-15s  %-15s%n", "Nodes", "Sparse (ms)", "Dense (ms)");
        System.out.println("-".repeat(44));

        for (int n : sizes) {
            WeightedGraph sparse = generateSparseGraph(n);
            WeightedGraph dense  = generateDenseGraph(n);

            double msSparse = measureFloydWarshall(sparse);
            double msDense  = measureFloydWarshall(dense);

            floydSparse.add(n, msSparse);
            floydDense.add(n, msDense);

            System.out.printf("n = %5d  ->  sparse: %.3f ms   dense: %.3f ms%n", n, msSparse, msDense);
        }

        generateImage(dijkstraSparse, dijkstraDense, "Dijkstra");
        generateImage(floydSparse,    floydDense,    "Floyd-Warshall");
    }
}