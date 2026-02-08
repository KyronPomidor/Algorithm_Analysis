import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.util.Arrays;

import javax.swing.JFrame;

public class Iterative {

    public static int iterative(int n) {
        int temp = 0;
        int a = 0;
        int b = 1;
        if (n <= 1) {
            return n;
        } else {
            for (int i = 2; i <= n; i++) {
                temp = a;
                a = b;
                b = temp + b;
            }
            return b;
        }
    }

   
    public static void main(String[] args) {

        int[] arr = { 501, 631, 794, 1000, 1259, 1585, 1995, 2512, 3162, 3981, 5012, 6310, 7943, 10000, 12589, 15849 };

        XYSeries series = new XYSeries("Fibonacci Recursive Time (ms)");

        for (int n : arr) {
            int[] m = new int[n + 1];
            Arrays.fill(m, -1);
            for (int i = 0; i < 1000; i++) {
                iterative(2); // Make JIT run here so that it does not affect the measurements of the time
            }

            long startTime = System.nanoTime();
            int fib = iterative(n);
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
