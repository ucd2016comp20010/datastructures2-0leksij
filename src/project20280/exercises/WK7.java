package project20280.exercises;

import project20280.interfaces.Entry;
import project20280.priorityqueue.HeapPriorityQueue;

import java.util.*;

/**
 * WK7 - Priority Queues & Heaps Exercise Solutions
 *
 * Q1:  Illustrate heap.insert() step by step
 * Q2:  Preorder traversal of final heap
 * Q3:  Postorder traversal of final heap
 * Q4:  Analysis of pre/post-order traversal properties
 * Q5:  HeapPriorityQueue already implemented (see HeapPriorityQueue.java)
 * Q6:  PQSort — sort using a HeapPriorityQueue
 * Q7:  In-place heapsort
 * Q8:  Leetcode 215 — Kth Largest Element in an Array
 * Q9:  Leetcode 658 — Find K Closest Elements
 * Q10: Job scheduling with priority + duration comparator
 */
public class WK7 {

    //
    // Q1: Illustrate heap.insert() step by step
    //
    // Input: [2, 5, 16, 4, 10, 23, 39, 18, 26, 15]
    // After each insert, upheap restores the heap property.
    //
    // insert(2):  [2]
    // insert(5):  [2, 5]             5 >= parent(2) → no swap
    // insert(16): [2, 5, 16]         16 >= parent(2) → no swap
    // insert(4):  [2, 4, 16, 5]      4 < parent(2) → swap(4,5) → done
    // insert(10): [2, 4, 16, 5, 10]  10 >= parent(4) → no swap
    // insert(23): [2, 4, 16, 5, 10, 23]  no swap
    // insert(39): [2, 4, 16, 5, 10, 23, 39]  no swap
    // insert(18): [2, 4, 16, 5, 10, 23, 39, 18]  18 >= parent(5) → no swap
    // insert(26): [2, 4, 16, 5, 10, 23, 39, 18, 26]  no swap
    // insert(15): [2, 4, 15, 5, 10, 16, 39, 18, 26, 23]
    //               15 added at index 9, parent=16 → swap(15,16)
    //               15 at index 2, parent=2 → 15>=2 → stop
    //
    // Final heap array: [2, 4, 15, 5, 10, 16, 39, 18, 26, 23]
    //
    //               2
    //          /        \
    //         4          15
    //       /   \       /   \
    //      5    10    16    39
    //    / \   /
    //  18  26 23
    //

    /**
     * Prints the heap state after each insert for Q1.
     */
    public static void demonstrateInserts() {
        int[] input = {2, 5, 16, 4, 10, 23, 39, 18, 26, 15};
        HeapPriorityQueue<Integer, Integer> pq = new HeapPriorityQueue<>();
        System.out.println("Q1: Step-by-step heap.insert()");
        for (int val : input) {
            pq.insert(val, val);
            System.out.println("  insert(" + val + ") → " + pq);
        }
        System.out.println();
    }

    //
    // Q2: Preorder traversal of the final heap
    //
    // Final heap: [2, 4, 15, 5, 10, 16, 39, 18, 26, 23]
    //
    // For an array-based heap at index i:
    //   left  = 2i+1,  right = 2i+2
    //
    // Preorder (root → left → right):
    //   2, 4, 5, 18, 26, 10, 23, 15, 16, 39
    //
    // Note: preorder does NOT guarantee descending order in general.
    //       Counter-example: 4 appears before 5, but 4 < 5, and later 10 < 15.
    //       However 2 (root/min) always appears first.
    //

    /**
     * Preorder traversal of an array-based heap (root→left→right).
     */
    public static <K, V> List<K> preorder(HeapPriorityQueue<K, V> pq) {
        List<K> result = new ArrayList<>();
        preorderHelper(pq, 0, result);
        return result;
    }


    //
    // Q3: Postorder traversal of the final heap
    //
    // Final heap: [2, 4, 15, 5, 10, 16, 39, 18, 26, 23]
    //
    // Postorder (left → right → root):
    //   18, 26, 5, 23, 10, 4, 16, 39, 15, 2
    //
    // Note: postorder does NOT guarantee ascending order in general.
    //       Counter-example: 18 appears before 5, but 18 > 5.
    //       However 2 (root/min) always appears last.
    //

    /**
     * Postorder traversal of an array-based heap (left→right→root).
     */
    public static <K, V> List<K> postorder(HeapPriorityQueue<K, V> pq) {
        List<K> result = new ArrayList<>();
        postorderHelper(pq, 0, result);
        return result;
    }

    private static <K, V> void preorderHelper(HeapPriorityQueue<K, V> pq, int i, List<K> result) {
        if (i >= pq.heapSize()) return;
        result.add(pq.heapGet(i).getKey());
        preorderHelper(pq, pq.leftIndex(i),  result);
        preorderHelper(pq, pq.rightIndex(i), result);
    }

    private static <K, V> void postorderHelper(HeapPriorityQueue<K, V> pq, int i, List<K> result) {
        if (i >= pq.heapSize()) return;
        postorderHelper(pq, pq.leftIndex(i),  result);
        postorderHelper(pq, pq.rightIndex(i), result);
        result.add(pq.heapGet(i).getKey());
    }

    //
    // Q4: Analysis of pre/post-order traversal properties
    //
    // PREORDER — does NOT always produce keys in descending order.
    // Counter-example: heap [2, 4, 15, 5, 10, 16, 39, 18, 26, 23]
    //   preorder = [2, 4, 5, 18, 26, 10, 23, 15, 16, 39]
    //   Not descending: 2 < 4 < 5 ... (it's actually roughly ascending for a min-heap)
    //
    // POSTORDER — does NOT always produce keys in ascending order.
    // Counter-example: same heap above
    //   postorder = [18, 26, 5, 23, 10, 4, 16, 39, 15, 2]
    //   Not ascending: 18 > 5, etc.
    //
    // What IS guaranteed:
    //   - Preorder: the root (minimum) is always first.
    //   - Postorder: the root (minimum) is always last.
    //   - Neither traversal produces a fully sorted sequence in general.
    //

    //
    // Q5: HeapPriorityQueue is already fully implemented in HeapPriorityQueue.java
    //

    //
    // Q6: PQSort — sort using HeapPriorityQueue
    //
    // Phase 1: Insert all n elements into the heap.       O(n log n)
    // Phase 2: Remove min n times to get sorted output.  O(n log n)
    // Total: O(n log n)
    //
    // Timing measurements show near-linear growth on a log scale,
    // consistent with O(n log n).
    //

    public static int[] pqSort(int[] arr) {
        HeapPriorityQueue<Integer, Integer> pq = new HeapPriorityQueue<>();
        for (int x : arr) pq.insert(x, x);
        int[] sorted = new int[arr.length];
        for (int i = 0; i < sorted.length; i++) {
            sorted[i] = pq.removeMin().getKey();
        }
        return sorted;
    }

    /**
     * Timing analysis for PQSort. Prints CSV: n,timeMs
     */
    public static void pqSortTiming() {
        System.out.println("Q6: PQSort timing (n, timeMs)");
        Random rng = new Random(42);
        for (int n = 1000; n <= 1_000_000; n += n < 10_000 ? 1000 : n < 100_000 ? 10_000 : 100_000) {
            int[] arr = rng.ints(n).toArray();
            long start = System.currentTimeMillis();
            pqSort(arr);
            long elapsed = System.currentTimeMillis() - start;
            System.out.printf("  n=%-8d  %d ms%n", n, elapsed);
        }
    }

    //
    // Q7: In-place heapsort
    //
    // Phase 1 — Build max-heap: call downheap on every internal node
    //   bottom-up (from n/2-1 down to 0).  O(n)
    //
    // Phase 2 — Sort: repeatedly swap root (max) with last element,
    //   shrink heap by 1, downheap root.  O(n log n)
    //
    // Total: O(n log n), no extra memory.
    //
    // Compared to PQSort:
    //   - Both are O(n log n)
    //   - In-place heapsort uses O(1) extra space vs O(n) for PQSort
    //   - In-place heapsort has better cache performance (single array)
    //

    public static void heapSort(int[] arr) {
        int n = arr.length;

        // Phase 1: build max-heap
        for (int i = n / 2 - 1; i >= 0; i--) {
            maxDownheap(arr, n, i);
        }

        // Phase 2: extract max repeatedly
        for (int end = n - 1; end > 0; end--) {
            // swap root (max) to sorted region
            int tmp = arr[0];
            arr[0] = arr[end];
            arr[end] = tmp;
            // restore heap property on reduced heap
            maxDownheap(arr, end, 0);
        }
    }

    /** Downheap for a max-heap stored in arr[0..heapSize-1] rooted at index i. */
    private static void maxDownheap(int[] arr, int heapSize, int i) {
        int largest = i;
        int left    = 2 * i + 1;
        int right   = 2 * i + 2;

        if (left  < heapSize && arr[left]  > arr[largest]) largest = left;
        if (right < heapSize && arr[right] > arr[largest]) largest = right;

        if (largest != i) {
            int tmp = arr[i];
            arr[i] = arr[largest];
            arr[largest] = tmp;
            maxDownheap(arr, heapSize, largest);
        }
    }

    /**
     * Timing comparison: PQSort vs heapSort. Prints CSV.
     */
    public static void sortComparison() {
        System.out.println("Q7: PQSort vs HeapSort timing");
        System.out.printf("  %-10s  %-12s  %-12s%n", "n", "pqSort(ms)", "heapSort(ms)");
        Random rng = new Random(42);
        for (int n = 1000; n <= 1_000_000; n += n < 10_000 ? 1000 : n < 100_000 ? 10_000 : 100_000) {
            int[] arr1 = rng.ints(n).toArray();
            int[] arr2 = arr1.clone();

            long t1 = System.currentTimeMillis();
            pqSort(arr1);
            long pqTime = System.currentTimeMillis() - t1;

            long t2 = System.currentTimeMillis();
            heapSort(arr2);
            long hsTime = System.currentTimeMillis() - t2;

            System.out.printf("  %-10d  %-12d  %-12d%n", n, pqTime, hsTime);
        }
    }

    //
    // Q8: Leetcode 215 — Kth Largest Element in an Array
    //
    // Approach: maintain a min-heap of size k.
    //   - For each element, add it to the heap.
    //   - If heap grows beyond k, remove the minimum.
    //   - After processing all elements, the heap's minimum IS the kth largest.
    //
    // Time:  O(n log k)   Space: O(k)
    //
    // Example: nums=[3,2,1,5,6,4], k=2  →  5
    //

    public static int findKthLargest(int[] nums, int k) {
        HeapPriorityQueue<Integer, Integer> minHeap = new HeapPriorityQueue<>();
        for (int num : nums) {
            minHeap.insert(num, num);
            if (minHeap.size() > k) {
                minHeap.removeMin();   // discard elements smaller than kth largest
            }
        }
        return minHeap.min().getKey();
    }

    //
    // Q9: Leetcode 658 — Find K Closest Elements
    //
    // Given sorted array arr, integer k and target x,
    // return the k closest elements to x as a sorted list.
    //
    // Approach: use a max-heap keyed by distance from x, size capped at k.
    //   - For each element compute distance = |arr[i] - x|
    //   - Maintain a max-heap of size k (heap stores negative distance as key
    //     so we can use our min-heap as a max-heap)
    //   - If heap > k, remove the element with largest distance
    //   - Collect remaining k elements and sort them
    //
    // Time: O(n log k + k log k)   Space: O(k)
    //
    // Example: arr=[1,2,3,4,5], k=4, x=3  →  [1,2,3,4]
    //

    public static List<Integer> findClosestElements(int[] arr, int k, int x) {
        // Use negative distance as key so our min-heap acts as a max-heap
        HeapPriorityQueue<Integer, Integer> maxHeap = new HeapPriorityQueue<>();

        for (int val : arr) {
            int dist = Math.abs(val - x);
            // Negate distance for max-heap behaviour; break ties by preferring smaller value
            // Tie-break: if same distance, larger value is "farther" so negate val too
            maxHeap.insert(-dist * 100_000 - val, val);
            if (maxHeap.size() > k) {
                maxHeap.removeMin();  // removes the "largest distance" element
            }
        }

        List<Integer> result = new ArrayList<>();
        while (!maxHeap.isEmpty()) {
            result.add(maxHeap.removeMin().getValue());
        }
        Collections.sort(result);
        return result;
    }

    //
    // Q10: Job Scheduling with priority + duration comparator
    //
    // Jobs have: priority (HIGH > MEDIUM > LOW) and expected duration.
    // Comparator: higher priority first; among equal priority, shorter job first.
    //
    // This models Shortest Job First within each priority band.
    //

    public enum JobPriority {
        LOW, MEDIUM, HIGH, CRITICAL   // ordinal increases with priority
    }

    public static class Job {
        private final String name;
        private final JobPriority priority;
        private final int expectedDurationSeconds;

        public Job(String name, JobPriority priority, int expectedDurationSeconds) {
            this.name = name;
            this.priority = priority;
            this.expectedDurationSeconds = expectedDurationSeconds;
        }

        @Override
        public String toString() {
            return name + "[" + priority + ", " + expectedDurationSeconds + "s]";
        }
    }

    /**
     * Comparator: higher priority first, then shorter duration first.
     * We negate priority ordinal so higher priority → smaller key.
     * We use duration as secondary key (shorter = smaller = better).
     */
    public static class JobComparator implements Comparator<Job> {
        @Override
        public int compare(Job a, Job b) {
            // Higher priority ordinal = more urgent → should come first (smaller key)
            int cmp = Integer.compare(b.priority.ordinal(), a.priority.ordinal());
            if (cmp != 0) return cmp;
            // Same priority → shorter duration first
            return Integer.compare(a.expectedDurationSeconds, b.expectedDurationSeconds);
        }
    }

    public static HeapPriorityQueue<Job, String> buildJobQueue(List<Job> jobs) {
        HeapPriorityQueue<Job, String> pq = new HeapPriorityQueue<>(new JobComparator());
        for (Job j : jobs) pq.insert(j, j.name);
        return pq;
    }

    public static void demonstrateJobScheduling() {
        List<Job> jobs = new ArrayList<>();
        jobs.add(new Job("BackupTask",       JobPriority.LOW,      3600));
        jobs.add(new Job("EmailAlert",       JobPriority.HIGH,     10));
        jobs.add(new Job("DataSync",         JobPriority.MEDIUM,   120));
        jobs.add(new Job("SystemShutdown",   JobPriority.CRITICAL, 5));
        jobs.add(new Job("LogRotation",      JobPriority.LOW,      60));
        jobs.add(new Job("HealthCheck",      JobPriority.HIGH,     30));
        jobs.add(new Job("ReportGen",        JobPriority.MEDIUM,   900));
        jobs.add(new Job("SecurityScan",     JobPriority.CRITICAL, 300));

        HeapPriorityQueue<Job, String> pq = buildJobQueue(jobs);

        System.out.println("Q10: Job scheduling order (CRITICAL/SHORT first):");
        while (!pq.isEmpty()) {
            System.out.println("  → " + pq.removeMin().getKey());
        }
    }



    public static void main(String[] args) {

        // ---- Q1 ----
        demonstrateInserts();

        // ---- Q2 ----
        System.out.println("Q2: Preorder traversal");
        Integer[] keys = {2, 5, 16, 4, 10, 23, 39, 18, 26, 15};
        HeapPriorityQueue<Integer, Integer> pq = new HeapPriorityQueue<>(keys, keys);
        System.out.println("  Heap:     " + pq);
        System.out.println("  Preorder: " + preorder(pq));
        // Expected: [2, 4, 5, 18, 26, 10, 23, 15, 16, 39]
        System.out.println();

        // ---- Q3 ----
        System.out.println("Q3: Postorder traversal");
        System.out.println("  Postorder: " + postorder(pq));
        // Expected: [18, 26, 5, 23, 10, 4, 16, 39, 15, 2]
        System.out.println();

        // ---- Q4 ----
        System.out.println("Q4: Traversal order analysis");
        System.out.println("  Preorder  does NOT always produce descending order.");
        System.out.println("  Postorder does NOT always produce ascending order.");
        System.out.println("  Counter-example above: preorder=" + preorder(pq));
        System.out.println("  Counter-example above: postorder=" + postorder(pq));
        System.out.println();

        // ---- Q6 ----
        System.out.println("Q6: PQSort demo");
        int[] arr = {5, 3, 8, 1, 9, 2};
        System.out.println("  Input:  " + Arrays.toString(arr));
        System.out.println("  Sorted: " + Arrays.toString(pqSort(arr)));
        System.out.println();
        pqSortTiming();
        System.out.println();

        // ---- Q7 ----
        System.out.println("Q7: In-place HeapSort demo");
        int[] arr2 = {5, 3, 8, 1, 9, 2};
        heapSort(arr2);
        System.out.println("  Sorted: " + Arrays.toString(arr2));
        System.out.println();
        sortComparison();
        System.out.println();

        // ---- Q8 ----
        System.out.println("Q8: Leetcode 215 — Kth Largest");
        System.out.println("  [3,2,1,5,6,4] k=2 → " + findKthLargest(new int[]{3,2,1,5,6,4}, 2)); // 5
        System.out.println("  [3,2,3,1,2,4,5,5,6] k=4 → " + findKthLargest(new int[]{3,2,3,1,2,4,5,5,6}, 4)); // 4
        System.out.println();

        // ---- Q9 ----
        System.out.println("Q9: Leetcode 658 — K Closest Elements");
        System.out.println("  [1,2,3,4,5] k=4 x=3 → " + findClosestElements(new int[]{1,2,3,4,5}, 4, 3)); // [1,2,3,4]
        System.out.println("  [1,2,3,4,5] k=4 x=-1 → " + findClosestElements(new int[]{1,2,3,4,5}, 4, -1)); // [1,2,3,4]
        System.out.println();

        // ---- Q10 ----
        demonstrateJobScheduling();
    }
}