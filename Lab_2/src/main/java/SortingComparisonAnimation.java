import java.util.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import javax.imageio.*;
import javax.imageio.metadata.*;
import javax.imageio.stream.*;

public class SortingComparisonAnimation {

    // GIF helpers 

    static void captureFrame(int[] arr, List<BufferedImage> frames, Color[] highlight) {
        int W = 800, H = 300;
        BufferedImage img = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, W, H);
        int max = Arrays.stream(arr).max().orElse(1);
        float barW = (float) W / arr.length;
        for (int i = 0; i < arr.length; i++) {
            g.setColor(highlight != null && highlight[i] != null ? highlight[i] : new Color(70, 130, 180));
            int h = (int) ((double) arr[i] / max * (H - 10));
            g.fillRect((int)(i * barW), H - h, Math.max(1, (int) barW - 1), h);
        }
        g.dispose();
        frames.add(img);
    }

    static void saveGif(List<BufferedImage> frames, String path, int delayCs) throws IOException {
        ImageWriter writer = ImageIO.getImageWritersByFormatName("gif").next();
        ImageOutputStream ios = new FileImageOutputStream(new File(path));
        writer.setOutput(ios);
        writer.prepareWriteSequence(null);
        for (BufferedImage frame : frames) {
            ImageWriteParam param = writer.getDefaultWriteParam();
            IIOMetadata meta = writer.getDefaultImageMetadata(
                    ImageTypeSpecifier.createFromRenderedImage(frame), param);
            String fmt = meta.getNativeMetadataFormatName();
            IIOMetadataNode root = (IIOMetadataNode) meta.getAsTree(fmt);
            IIOMetadataNode gce = getOrCreateNode(root, "GraphicControlExtension");
            gce.setAttribute("delayTime", String.valueOf(delayCs));
            gce.setAttribute("disposalMethod", "doNotDispose");
            gce.setAttribute("userInputFlag", "FALSE");
            gce.setAttribute("transparentColorFlag", "FALSE");
            gce.setAttribute("transparentColorIndex", "0");
            IIOMetadataNode appExts = getOrCreateNode(root, "ApplicationExtensions");
            IIOMetadataNode appExt = new IIOMetadataNode("ApplicationExtension");
            appExt.setAttribute("applicationID", "NETSCAPE");
            appExt.setAttribute("authenticationCode", "2.0");
            appExt.setUserObject(new byte[]{1, 0, 0});
            appExts.appendChild(appExt);
            meta.setFromTree(fmt, root);
            writer.writeToSequence(new IIOImage(frame, null, meta), param);
        }
        writer.endWriteSequence();
        ios.close();
        System.out.println("Saved → " + path + " (" + frames.size() + " frames)");
    }

    static IIOMetadataNode getOrCreateNode(IIOMetadataNode root, String name) {
        for (int i = 0; i < root.getLength(); i++)
            if (root.item(i).getNodeName().equals(name))
                return (IIOMetadataNode) root.item(i);
        IIOMetadataNode node = new IIOMetadataNode(name);
        root.appendChild(node);
        return node;
    }

    // Highlight color constants 

    static final Color C_PIVOT  = new Color(220, 50,  50);  // red    - pivot / right pointer
    static final Color C_ACTIVE = new Color(255, 200,  0);  // yellow - element being compared
    static final Color C_SORTED = new Color(60,  179, 113); // green  - confirmed sorted
    static final Color C_WINDOW = new Color(150, 180, 255); // light blue - merge window

    // Quick Sort 

    static int partition(int[] arr, int start, int end) {
        int pivot = arr[end], i = start - 1;
        for (int j = start; j <= end - 1; j++) {
            if (arr[j] < pivot) { i++; int t = arr[i]; arr[i] = arr[j]; arr[j] = t; }
        }
        i++; int t = arr[i]; arr[i] = arr[end]; arr[end] = t;
        return i;
    }

    static int partitionAnim(int[] arr, int start, int end, List<BufferedImage> frames) {
        int pivot = arr[end], i = start - 1;
        for (int j = start; j <= end - 1; j++) {
            Color[] hl = new Color[arr.length];
            hl[end] = C_PIVOT;
            hl[j]   = C_ACTIVE;
            if (i >= start) hl[i] = new Color(255, 140, 0);
            captureFrame(arr, frames, hl);
            if (arr[j] < pivot) { i++; int t = arr[i]; arr[i] = arr[j]; arr[j] = t; }
        }
        i++; int t = arr[i]; arr[i] = arr[end]; arr[end] = t;
        Color[] hl = new Color[arr.length];
        hl[i] = C_PIVOT;
        captureFrame(arr, frames, hl);
        return i;
    }

    static void quickSortBetterAnim(int[] arr, int start, int end, List<BufferedImage> frames) {
        while (start < end) {
            medianOfThree(arr, start, end);
            int pivot = partitionAnim(arr, start, end, frames);
            if (pivot - start < end - pivot) {
                quickSortBetterAnim(arr, start, pivot - 1, frames);
                start = pivot + 1;
            } else {
                quickSortBetterAnim(arr, pivot + 1, end, frames);
                end = pivot - 1;
            }
        }
    }

    static void medianOfThree(int[] arr, int start, int end) {
        int mid = start + (end - start) / 2;
        if (arr[start] > arr[mid]) { int t = arr[start]; arr[start] = arr[mid];  arr[mid]  = t; }
        if (arr[start] > arr[end]) { int t = arr[start]; arr[start] = arr[end];  arr[end]  = t; }
        if (arr[mid]   > arr[end]) { int t = arr[mid];   arr[mid]   = arr[end];  arr[end]  = t; }
        int temp = arr[mid]; arr[mid] = arr[end]; arr[end] = temp;
    }

    // Merge Sort

    static void mergeSortAnim(int[] arr, int from, int to, List<BufferedImage> frames) {
        if (to - from <= 1) return;
        int mid = from + (to - from) / 2;
        mergeSortAnim(arr, from, mid, frames);
        mergeSortAnim(arr, mid,  to,  frames);
        mergeAnim(arr, from, mid, to, frames);
    }

    static void mergeAnim(int[] arr, int from, int mid, int to, List<BufferedImage> frames) {
        int[] tmp = Arrays.copyOfRange(arr, from, to);
        int l = 0, r = mid - from, i = from;
        while (l < mid - from && r < to - from) {
            Color[] hl = new Color[arr.length];
            for (int k = from; k < to; k++) hl[k] = C_WINDOW;
            hl[from + l] = C_ACTIVE;
            hl[from + r] = C_PIVOT;
            captureFrame(arr, frames, hl);
            arr[i++] = (tmp[l] <= tmp[r]) ? tmp[l++] : tmp[r++];
        }
        while (l < mid - from) arr[i++] = tmp[l++];
        while (r < to - from)  arr[i++] = tmp[r++];
        Color[] hl = new Color[arr.length];
        for (int k = from; k < to; k++) hl[k] = C_SORTED;
        captureFrame(arr, frames, hl);
    }

    //  Heap Sort 

    static void heapifyAnim(int[] arr, int n, int root, int sortedFrom, List<BufferedImage> frames) {
        int largest = root, left = 2 * root + 1, right = 2 * root + 2;
        if (left  < n && arr[left]  > arr[largest]) largest = left;
        if (right < n && arr[right] > arr[largest]) largest = right;
        if (largest != root) {
            Color[] hl = new Color[arr.length];
            hl[root] = C_ACTIVE; hl[largest] = C_PIVOT;
            for (int k = sortedFrom; k < arr.length; k++) hl[k] = C_SORTED;
            captureFrame(arr, frames, hl);
            int temp = arr[root]; arr[root] = arr[largest]; arr[largest] = temp;
            heapifyAnim(arr, n, largest, sortedFrom, frames);
        }
    }

    static void heapSortAnim(int[] arr, List<BufferedImage> frames) {
        int n = arr.length;
        for (int i = n / 2 - 1; i >= 0; i--) heapify(arr, n, i);
        captureFrame(arr, frames, null);
        for (int i = n - 1; i > 0; i--) {
            Color[] hl = new Color[arr.length];
            hl[0] = C_PIVOT; hl[i] = C_ACTIVE;
            for (int k = i + 1; k < n; k++) hl[k] = C_SORTED;
            captureFrame(arr, frames, hl);
            int temp = arr[0]; arr[0] = arr[i]; arr[i] = temp;
            heapifyAnim(arr, i, 0, i, frames);
        }
    }

    static void heapify(int[] arr, int n, int root) {
        int largest = root, left = 2 * root + 1, right = 2 * root + 2;
        if (left  < n && arr[left]  > arr[largest]) largest = left;
        if (right < n && arr[right] > arr[largest]) largest = right;
        if (largest != root) {
            int temp = arr[root]; arr[root] = arr[largest]; arr[largest] = temp;
            heapify(arr, n, largest);
        }
    }

    //  Bubble Sort

    // [NEW] frameEvery: only capture 1 frame every N comparisons to keep GIF small
    static void bubbleSortAnim(int[] arr, List<BufferedImage> frames) {
        int frameEvery = 5; // [NEW] capture every 5th comparison (~1000 → ~200 frames)
        int cmp = 0;        // [NEW] comparison counter
        for (int i = 0; i < arr.length - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < arr.length - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j]; arr[j] = arr[j + 1]; arr[j + 1] = temp;
                    swapped = true;
                }
                // [NEW] only emit a frame every frameEvery comparisons
                if (++cmp % frameEvery == 0) {
                    Color[] hl = new Color[arr.length];
                    hl[j]   = C_ACTIVE;
                    hl[j+1] = C_PIVOT;
                    for (int k = arr.length - i; k < arr.length; k++) hl[k] = C_SORTED;
                    captureFrame(arr, frames, hl);
                }
            }
            if (!swapped) break;
        }
        // [NEW] always emit a final fully-sorted frame
        Color[] hl = new Color[arr.length];
        Arrays.fill(hl, C_SORTED);
        captureFrame(arr, frames, hl);
    }

    //  Array Generator

    static int[] generateRandom(int size) {
        Random rand = new Random(42);
        int[] arr = new int[size];
        for (int i = 0; i < size; i++) arr[i] = rand.nextInt(100000);
        return arr;
    }

    static void generateAnimations() {
        int[] base = generateRandom(100);
        System.out.println("Generating GIF animations for n=100...");
        try {
            List<BufferedImage> qf = new ArrayList<>();
            int[] qa = Arrays.copyOf(base, base.length);
            captureFrame(qa, qf, null);
            quickSortBetterAnim(qa, 0, qa.length - 1, qf);
            saveGif(qf, "quick_sort.gif", 6);

            List<BufferedImage> mf = new ArrayList<>();
            int[] ma = Arrays.copyOf(base, base.length);
            captureFrame(ma, mf, null);
            mergeSortAnim(ma, 0, ma.length, mf);
            saveGif(mf, "merge_sort.gif", 6);

            List<BufferedImage> hf = new ArrayList<>();
            int[] ha = Arrays.copyOf(base, base.length);
            captureFrame(ha, hf, null);
            heapSortAnim(ha, hf);
            saveGif(hf, "heap_sort.gif", 6);

            List<BufferedImage> bf = new ArrayList<>();
            int[] ba = Arrays.copyOf(base, base.length);
            captureFrame(ba, bf, null);
            bubbleSortAnim(ba, bf);
            saveGif(bf, "bubble_sort.gif", 6);
        } catch (IOException e) {
            System.err.println("GIF error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        generateAnimations();
    }
}