package project20280.treapproject;

import project20280.interfaces.Entry;
import project20280.priorityqueue.HeapPriorityQueue;

import java.util.*;

/**
 * Sorting benchmark: TreapSort vs PQSort vs TimSort vs QuickSort vs MergeSort.
 *
 * <h3>Algorithms</h3>
 * <dl>
 *   <dt>TreapSort</dt>
 *   <dd>Insert all elements into a {@link TreapMap}; retrieve with entrySet()
 *       (in-order traversal). O(n log n) expected.</dd>
 *
 *   <dt>PQSort</dt>
 *   <dd>Insert all elements into a {@link HeapPriorityQueue}; extract-min
 *       n times. O(n log n).</dd>
 *
 *   <dt>TimSort</dt>
 *   <dd>{@code Collections.sort()} on an ArrayList – Java's built-in
 *       Timsort hybrid. O(n log n), adaptive.</dd>
 *
 *   <dt>QuickSort</dt>
 *   <dd>Recursive randomised pivot (Lomuto partition). O(n log n) expected.</dd>
 *
 *   <dt>MergeSort</dt>
 *   <dd>Classic top-down merge sort. O(n log n) worst case.</dd>
 * </dl>
 *
 * <h3>Input patterns</h3>
 * Random, Nearly Sorted (95 % in place), Reverse Sorted.
 *
 * <h3>Sizes</h3>
 * n ∈ {100, 500, 1 000, 2 000, 5 000, 10 000}.
 *
 * <p>Each cell is the median of {@value #MEASURE_RUNS} independent timed runs
 * (preceded by {@value #WARMUP_RUNS} warm-up runs).
 */
public class SortingBenchmark {


    // Configuration
    private static final int[] SIZES        = {100, 500, 1_000, 2_000, 5_000, 10_000};
    private static final int   WARMUP_RUNS  = 3;
    private static final int   MEASURE_RUNS = 7;
    private static final long  SEED         = 42L;


    // Input-pattern generators


    enum Pattern { RANDOM, NEARLY_SORTED, REVERSE }

    private static int[] generate(int n, Pattern p, Random rng) {
        int[] arr = new int[n];
        switch (p) {
            case RANDOM:
                for (int i = 0; i < n; i++) arr[i] = rng.nextInt(n * 10);
                break;
            case NEARLY_SORTED: {
                for (int i = 0; i < n; i++) arr[i] = i;
                int swaps = Math.max(1, n / 20);  // swap ~5 % of pairs
                for (int s = 0; s < swaps; s++) {
                    int a = rng.nextInt(n), b = rng.nextInt(n);
                    int tmp = arr[a]; arr[a] = arr[b]; arr[b] = tmp;
                }
                break;
            }
            default: // REVERSE
                for (int i = 0; i < n; i++) arr[i] = n - i;
        }
        return arr;
    }


    // Sorting algorithms


    //TreapSort

    /**
     * Sorts {@code arr} by inserting all elements into a TreapMap (key=value)
     * and reading them back via in-order traversal.
     *
     * <p>Duplicate values are handled by using (value, index) as composite key
     * to avoid key collisions.
     */
    static int[] treapSort(int[] arr) {
        // Use a TreapMap<Long, Integer> where key = (value << 20 | index)
        // so that duplicate values still get distinct keys.
        TreapMap<Long, Integer> treap = new TreapMap<>();
        for (int i = 0; i < arr.length; i++) {
            long compositeKey = ((long) arr[i] << 20) | i;
            treap.put(compositeKey, arr[i]);
        }
        int[] sorted = new int[arr.length];
        int idx = 0;
        for (Entry<Long, Integer> e : treap.entrySet()) {
            sorted[idx++] = e.getValue();
        }
        return sorted;
    }

    //PQSort

    // Sorts using a min-heap priority queue (your HeapPriorityQueue).
    static int[] pqSort(int[] arr) {
        HeapPriorityQueue<Integer, Integer> pq = new HeapPriorityQueue<>();
        for (int x : arr) pq.insert(x, x);
        int[] sorted = new int[arr.length];
        for (int i = 0; i < sorted.length; i++) {
            sorted[i] = pq.removeMin().getKey();
        }
        return sorted;
    }

    //TimSort (Collections.sort)

    //Sorts using Java's built-in Timsort via {@code Collections.sort()}.
    static int[] timSort(int[] arr) {
        List<Integer> list = new ArrayList<>(arr.length);
        for (int x : arr) list.add(x);
        Collections.sort(list);
        int[] sorted = new int[arr.length];
        for (int i = 0; i < sorted.length; i++) sorted[i] = list.get(i);
        return sorted;
    }

    //QuickSort

    /** Randomised QuickSort (Lomuto partition scheme). */
    static void quickSort(int[] arr, int lo, int hi, Random rng) {
        if (lo >= hi) return;
        // random pivot to avoid worst-case on sorted input
        int pivotIdx = lo + rng.nextInt(hi - lo + 1);
        int tmp = arr[pivotIdx]; arr[pivotIdx] = arr[hi]; arr[hi] = tmp;

        int pivot = arr[hi], i = lo - 1;
        for (int j = lo; j < hi; j++) {
            if (arr[j] <= pivot) { i++; tmp = arr[i]; arr[i] = arr[j]; arr[j] = tmp; }
        }
        tmp = arr[i + 1]; arr[i + 1] = arr[hi]; arr[hi] = tmp;
        int p = i + 1;

        quickSort(arr, lo,     p - 1, rng);
        quickSort(arr, p + 1,  hi,    rng);
    }

    static int[] quickSort(int[] arr) {
        int[] copy = arr.clone();
        quickSort(copy, 0, copy.length - 1, new Random(SEED));
        return copy;
    }

    //MergeSort

    // Classic top-down merge sort.
    static void mergeSort(int[] arr, int[] tmp, int lo, int hi) {
        if (lo >= hi) return;
        int mid = (lo + hi) >>> 1;
        mergeSort(arr, tmp, lo,      mid);
        mergeSort(arr, tmp, mid + 1, hi);
        merge(arr, tmp, lo, mid, hi);
    }

    private static void merge(int[] arr, int[] tmp, int lo, int mid, int hi) {
        System.arraycopy(arr, lo, tmp, lo, hi - lo + 1);
        int i = lo, j = mid + 1, k = lo;
        while (i <= mid && j <= hi) {
            arr[k++] = (tmp[i] <= tmp[j]) ? tmp[i++] : tmp[j++];
        }
        while (i <= mid)  arr[k++] = tmp[i++];
        while (j <= hi)   arr[k++] = tmp[j++];
    }

    static int[] mergeSort(int[] arr) {
        int[] copy = arr.clone();
        int[] tmp  = new int[copy.length];
        mergeSort(copy, tmp, 0, copy.length - 1);
        return copy;
    }


    // Correctness check
    private static boolean isSorted(int[] arr) {
        for (int i = 0; i < arr.length - 1; i++) {
            if (arr[i] > arr[i + 1]) return false;
        }
        return true;
    }


    // Timing
    @FunctionalInterface interface SortFn { int[] sort(int[] arr); }

    private static long medianUs(SortFn fn, int[] arr) {
        long[] ns = new long[MEASURE_RUNS];
        for (int r = 0; r < MEASURE_RUNS; r++) {
            long t0 = System.nanoTime();
            int[] result = fn.sort(arr);
            ns[r] = System.nanoTime() - t0;
            // consume result to prevent dead-code elimination
            if (result.length < 0) throw new IllegalStateException();
        }
        Arrays.sort(ns);
        return ns[MEASURE_RUNS / 2] / 1_000;
    }


    // Main
    public static void main(String[] args) {

        //correctness check
        System.out.println("Correctness check:");
        int[] sample = {5, 3, 8, 1, 9, 2, 7, 4, 6, 0, 3, 9, 1};
        int[][] results = {
            treapSort(sample),
            pqSort(sample),
            timSort(sample),
            quickSort(sample),
            mergeSort(sample)
        };
        String[] names = {"TreapSort", "PQSort", "TimSort", "QuickSort", "MergeSort"};
        for (int i = 0; i < results.length; i++) {
            System.out.printf("  %-12s sorted=%b  %s%n",
                names[i], isSorted(results[i]), Arrays.toString(results[i]));
        }
        System.out.println();

        // -- benchmark -- //
        final String HR = "=".repeat(100);
        final String hr = "─".repeat(100);

        System.out.println(HR);
        System.out.println("  Sorting Benchmark — times in µs, median of " + MEASURE_RUNS + " runs");
        System.out.println(HR);

        Random rng = new Random(SEED);

        // Warm-up JIT with a single large random array
        int[] warmupArr = generate(10_000, Pattern.RANDOM, new Random(0));
        for (int w = 0; w < WARMUP_RUNS; w++) {
            treapSort(warmupArr); pqSort(warmupArr);
            timSort(warmupArr);   quickSort(warmupArr); mergeSort(warmupArr);
        }

        String header = String.format("  %-16s %6s %12s %10s %10s %12s %12s",
            "Pattern", "n", "TreapSort", "PQSort", "TimSort", "QuickSort", "MergeSort");

        for (Pattern pattern : Pattern.values()) {
            System.out.println();
            System.out.println(hr);
            System.out.printf("  Pattern: %s%n", pattern);
            System.out.println(hr);
            System.out.println(header);
            System.out.println("  " + "─".repeat(90));

            for (int n : SIZES) {
                int[] arr = generate(n, pattern, rng);

                long tTreap = medianUs(SortingBenchmark::treapSort, arr);
                long tPQ    = medianUs(SortingBenchmark::pqSort,    arr);
                long tTim   = medianUs(SortingBenchmark::timSort,   arr);
                long tQS    = medianUs(SortingBenchmark::quickSort,  arr);
                long tMS    = medianUs(SortingBenchmark::mergeSort,  arr);

                System.out.printf("  %-16s %6d %12d %10d %10d %12d %12d%n",
                    pattern, n, tTreap, tPQ, tTim, tQS, tMS);
            }
        }

        System.out.println();
        System.out.println(HR);
        System.out.println("Benchmark complete.");
        System.out.println();
        System.out.println("Notes:");
        System.out.println("  * TreapSort and PQSort use O(n) extra space.");
        System.out.println("  * MergeSort uses O(n) extra space (auxiliary array).");
        System.out.println("  * QuickSort is in-place, O(log n) stack space expected.");
        System.out.println("  * TimSort (Collections.sort) is adaptive — fastest on nearly-sorted data.");
        System.out.println("  * TreapSort handles duplicates via a composite (value, index) key.");
    }
}
