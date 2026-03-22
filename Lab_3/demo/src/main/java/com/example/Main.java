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

    // Builds a graph with n nodes and random edges
    static Graph generateGraph(int n) {
        Graph graph = new Graph();
        Random rnd = new Random();

        for (int i = 0; i < n; i++)
            graph.addNode(new Node((char) ('A' + (i % 26))));

        int edges = n * 2;
        for (int i = 0; i < n - 1; i++) {
            graph.addEdge(i, i + 1);
        }

        for (int i = 0; i < edges; i++) {
            int src = rnd.nextInt(n);
            int dst = rnd.nextInt(n);
            if (src != dst && !graph.checkNode(src, dst))
                graph.addEdge(src, dst);
        }

        return graph;
    }

    static double measureDFS(Graph graph) {
        int runs = 5;
        long total = 0;
        for (int i = 0; i < runs; i++) {
            long t0 = System.nanoTime();
            graph.DepthFirstSearch(0);
            total += System.nanoTime() - t0;
        }
        return (total / runs) / 1000000d;
    }

    static double measureBFS(Graph graph) {
        int runs = 5;
        long total = 0;
        for (int i = 0; i < runs; i++) {
            long t0 = System.nanoTime();
            graph.BreadthFirstSearch(0);
            total += System.nanoTime() - t0;
        }
        return (total / runs) / 1000000d;
    }

    static void generateImage(XYSeries series, String name) {
        XYSeriesCollection dataset = new XYSeriesCollection(series);

        JFreeChart chart = ChartFactory.createXYLineChart(
                name + " Execution Time vs Graph Size",
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
        r.setSeriesPaint(0, new Color(0x37, 0x8A, 0xDD));
        r.setSeriesStroke(0, new BasicStroke(2.0f));
        r.setSeriesShapesVisible(0, true);
        plot.setRenderer(r);

        chart.getTitle().setFont(new Font("SansSerif", Font.BOLD, 14));
        plot.getDomainAxis().setTickLabelFont(new Font("SansSerif", Font.PLAIN, 11));
        plot.getRangeAxis().setTickLabelFont(new Font("SansSerif", Font.PLAIN, 11));

        try {
            BufferedImage img = chart.createBufferedImage(700, 420);
            ImageIO.write(img, "png", new File(name + "_chart.png"));
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        int[] sizes = { 10, 50, 100, 500, 1000, 2000, 4000 };

        System.out.println("DFS:\n");

        XYSeries seriesDFS = new XYSeries("DFS time");

        for (int n : sizes) {
            Graph graph = generateGraph(n);
            double ms = measureDFS(graph);
            seriesDFS.add(n, ms);
            System.out.printf("n = %5d  ->  %.3f ms%n", n, ms);
        }

        System.out.println("\nBFS:\n");

        XYSeries seriesBFS = new XYSeries("BFS time");

        for (int n : sizes) {
            Graph graph = generateGraph(n);
            double ms = measureBFS(graph);
            seriesBFS.add(n, ms);
            System.out.printf("n = %5d  ->  %.3f ms%n", n, ms);
        }

        // Generate the chart pictures
        generateImage(seriesDFS, "DFS");
        generateImage(seriesBFS, "BFS");
    }

}