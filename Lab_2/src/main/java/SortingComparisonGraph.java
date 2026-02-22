import java.util.*;
import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

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

        // Extract elements from heap one by one
        for (int i = n - 1; i > 0; i--) {
            // Move current root (max) to end
            int temp = arr[0];
            arr[0] = arr[i];
            arr[i] = temp;
            // Heapify the reduced heap
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
        int[] copy1 = Arrays.copyOf(arr, arr.length);
        long start = System.nanoTime();
        quickSort(copy1, 0, copy1.length - 1);
        long qTime = System.nanoTime() - start;

        int[] copy2 = Arrays.copyOf(arr, arr.length);
        start = System.nanoTime();
        mergeSort(copy2);
        long mTime = System.nanoTime() - start;

        int[] copy3 = Arrays.copyOf(arr, arr.length);
        start = System.nanoTime();
        heapSort(copy3);
        long hTime = System.nanoTime() - start;

        int[] copy4 = Arrays.copyOf(arr, arr.length);
        start = System.nanoTime();
        bubbleSort(copy4);
        long bTime = System.nanoTime() - start;

        return new long[] { qTime, mTime, hTime, bTime };
    }

    // Run Tests and Collect Data
    static Map<String, long[]> runAllTests() {
        Map<String, long[]> results = new LinkedHashMap<>();
        int[] sizes = { 100, 10000, 15000 };

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

        JFreeChart chart = ChartFactory.createBarChart(
                "Sorting Benchmark",
                "Array Type & Size",
                "Time (ms)",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        JFrame frame = new JFrame("Sorting Benchmark Chart");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new ChartPanel(chart));
        frame.pack();
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