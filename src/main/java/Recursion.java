import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.util.Arrays;

import javax.swing.JFrame;

public class Recursion {

    public static int recursion(int n) {
        if (n <= 1) {
            return n;
        } else {
            return recursion(n - 1) + recursion(n - 2);
        }
    }

    public static void main(String[] args) {

        int[] arr = { 5, 7, 10, 12, 15, 17, 20, 22, 25, 27, 30, 32, 35, 37, 40, 42, 45 };

        XYSeries series = new XYSeries("Fibonacci Recursive Time (ms)");

        for (int n : arr) {
            int[] m = new int[n + 1];
            Arrays.fill(m, -1);
            for (int i = 0; i < 1000; i++) {
                recursion(2); // Make JIT run here so that it does not affect the measurements of the time
            }

            long startTime = System.nanoTime();
            int fib = recursion(n);
            long endTime = System.nanoTime();

            double duration = (double) (endTime - startTime) / 1_000_000; // ms
            System.out.println(n + ". (Time: " + duration + " ms)");
            
            series.add(n, duration);
        }
        // Create dataset
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);

        // Create chart
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Fibonacci Recursive Time",
                "n (Fibonacci index)",
                "Time (ms)",
                dataset);

        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        renderer.setSeriesLinesVisible(0, true); // show lines
        renderer.setSeriesShapesVisible(0, true); // show points

        plot.setRenderer(renderer);

        JFrame frame = new JFrame("Fibonacci Time Graph");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new ChartPanel(chart));
        frame.pack();
        frame.setVisible(true);
    }
}
