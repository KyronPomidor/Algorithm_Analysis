import java.util.*;
import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import java.awt.BasicStroke;
import java.awt.Color;

public class SortingComparisonGraph {

    // Quick Sort
    static void quickSort(int[] arr, int start, int end) {
        if (end <= start)
            return;
        int pivot = partition(arr, start, end);
        quickSort(arr, start, pivot - 1);
        quickSort(arr, pivot + 1, end);
    }

    static int partition(int[] arr, int start, int end) {
        int pivot = arr[end], i = start - 1;
        for (int j = start; j <= end - 1; j++) {
            if (arr[j] < pivot) {
                i++;
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }
        i++;
        int temp = arr[i];
        arr[i] = arr[end];
        arr[end] = temp;
        return i;
    }

    // Better Quick Sort
    static void quickSortBetter(int[] arr, int start, int end) {
        while (start < end) {

            // Choose better pivot
            medianOfThree(arr, start, end);
            int pivot = partition(arr, start, end);

            // Recurse into smaller partition first
            if (pivot - start < end - pivot) {
                quickSortBetter(arr, start, pivot - 1);
                start = pivot + 1;
            } else {
                quickSortBetter(arr, pivot + 1, end);
                end = pivot - 1;
            }
        }
    }

    static void medianOfThree(int[] arr, int start, int end) {
        int mid = start + (end - start) / 2;

        // Sort start, mid, end so median ends up at mid
        if (arr[start] > arr[mid]) {
            int temp = arr[start];
            arr[start] = arr[mid];
            arr[mid] = temp;
        }
        if (arr[start] > arr[end]) {
            int temp = arr[start];
            arr[start] = arr[end];
            arr[end] = temp;
        }
        if (arr[mid] > arr[end]) {
            int temp = arr[mid];
            arr[mid] = arr[end];
            arr[end] = temp;
        }

        // Place median at end so partition() picks it up as usual
        int temp = arr[mid];
        arr[mid] = arr[end];
        arr[end] = temp;
    }

    // Merge Sort
    static void mergeSort(int[] arr) {
        int n = arr.length;
        if (n <= 1)
            return;
        int mid = n / 2;
        int[] left = Arrays.copyOfRange(arr, 0, mid);
        int[] right = Arrays.copyOfRange(arr, mid, n);
        mergeSort(left);
        mergeSort(right);
        merge(left, right, arr);
    }

    static void merge(int[] left, int[] right, int[] arr) {
        int i = 0, l = 0, r = 0;
        while (l < left.length && r < right.length)
            arr[i++] = (left[l] < right[r]) ? left[l++] : right[r++];

        while (l < left.length)
            arr[i++] = left[l++];
        while (r < right.length)
            arr[i++] = right[r++];
    }

    // Heap Sort
    static void heapSort(int[] arr) {
        int n = arr.length;

        // Build max-heap
        for (int i = n / 2 - 1; i >= 0; i--)
            heapify(arr, n, i);

        for (int i = n - 1; i > 0; i--) {
            // Move current root (max) to end
            int temp = arr[0];
            arr[0] = arr[i];
            arr[i] = temp;

            heapify(arr, i, 0);
        }
    }

    static void heapify(int[] arr, int n, int root) {
        int largest = root;
        int left = 2 * root + 1;
        int right = 2 * root + 2;

        if (left < n && arr[left] > arr[largest])
            largest = left;
        if (right < n && arr[right] > arr[largest])
            largest = right;

        if (largest != root) {
            int temp = arr[root];
            arr[root] = arr[largest];
            arr[largest] = temp;
            heapify(arr, n, largest);
        }
    }

    // Bubble Sort
    static void bubbleSort(int[] arr) {
        for (int i = 0; i < arr.length - 1; i++) {
            boolean alreadySorted = false;
            for (int j = 0; j < arr.length - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                    alreadySorted = true;
                }
            }
            if (!alreadySorted)
                break;
        }
    }

    // Array Generators
    static int[] generateRandom(int size) {
        Random rand = new Random(42);
        int[] arr = new int[size];
        for (int i = 0; i < size; i++)
            arr[i] = rand.nextInt(100000);
        return arr;
    }

    static int[] generateSorted(int size) {
        int[] arr = new int[size];
        for (int i = 0; i < size; i++)
            arr[i] = i;
        return arr;
    }

    static int[] generateDescending(int size) {
        int[] arr = new int[size];
        for (int i = 0; i < size; i++)
            arr[i] = size - i;
        return arr;
    }

    // Benchmark - returns 4 times: [quick, merge, heap, bubble]
    static long[] benchmark(int[] arr) {
        long total = 0;

        for (int i = 0; i < 4; i++) {
            int[] copy = Arrays.copyOf(arr, arr.length);
            long start = System.nanoTime();
            quickSortBetter(copy, 0, copy.length - 1);
            total += System.nanoTime() - start;
        }

        long qTime = total / 4;

        total = 0;
        for (int i = 0; i < 4; i++) {
            int[] copy2 = Arrays.copyOf(arr, arr.length);
            long start = System.nanoTime();
            mergeSort(copy2);
            total += System.nanoTime() - start;
        }

        long mTime = total / 4;

        total = 0;
        for (int i = 0; i < 4; i++) {
            int[] copy3 = Arrays.copyOf(arr, arr.length);
            long start = System.nanoTime();
            heapSort(copy3);
            total += System.nanoTime() - start;
        }

        long hTime = total / 4;

        total = 0;
        for (int i = 0; i < 1; i++) {
            int[] copy4 = Arrays.copyOf(arr, arr.length);
            long start = System.nanoTime();
            bubbleSort(copy4);
            total += System.nanoTime() - start;
        }
        long bTime = total / 1;

        return new long[] { qTime, mTime, hTime, bTime };
    }

    // Run Tests and Collect Data
    static Map<String, long[]> runAllTests() {
        Map<String, long[]> results = new LinkedHashMap<>();
        int[] sizes = { 100, 10000, 100000 };

        for (int size : sizes) {
            results.put("Random " + size, benchmark(generateRandom(size)));
            results.put("Sorted " + size, benchmark(generateSorted(size)));
            results.put("Descending " + size, benchmark(generateDescending(size)));
        }
        return results;
    }

    // Generate Graph
    static void createChart(Map<String, long[]> results) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Map.Entry<String, long[]> entry : results.entrySet()) {
            String label = entry.getKey();
            long[] times = entry.getValue();
            dataset.addValue(times[0] / 1000000.0, "Quick Sort", label);
            dataset.addValue(times[1] / 1000000.0, "Merge Sort", label);
            dataset.addValue(times[2] / 1000000.0, "Heap Sort", label);
            dataset.addValue(times[3] / 1000000.0, "Bubble Sort", label);
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "Sorting Algorithm Benchmark",
                "Array Type & Size",
                "Time (ms)",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        // Style the plot
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setDomainGridlinesVisible(true);

        // Make lines thicker and add data point shapes
        LineAndShapeRenderer renderer = new LineAndShapeRenderer();
        renderer.setDefaultShapesVisible(true);
        renderer.setDefaultStroke(new BasicStroke(2.0f));
        renderer.setSeriesPaint(0, new Color(70, 130, 180)); // Quick - blue
        renderer.setSeriesPaint(1, new Color(60, 179, 113)); // Merge - green
        renderer.setSeriesPaint(2, new Color(255, 165, 0)); // Heap - orange
        renderer.setSeriesPaint(3, new Color(220, 50, 50)); // Bubble - red
        plot.setRenderer(renderer);

        // Rotate x-axis labels so they don't overlap
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

        JFrame frame = new JFrame("Sorting Benchmark Chart");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new ChartPanel(chart));
        frame.setSize(1000, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        System.out.println("========== Sorting Benchmark ==========");
        Map<String, long[]> results = runAllTests();

        for (var e : results.entrySet()) {
            System.out.printf("%-20s: Quick = %.3f ms, Merge = %.3f ms, Heap = %.3f ms, Bubble = %.3f ms\n",
                    e.getKey(),
                    e.getValue()[0] / 1e6,
                    e.getValue()[1] / 1e6,
                    e.getValue()[2] / 1e6,
                    e.getValue()[3] / 1e6);
        }

        createChart(results);
    }
}