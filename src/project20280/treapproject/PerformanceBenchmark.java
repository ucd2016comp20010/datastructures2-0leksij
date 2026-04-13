package project20280.treapproject;

import project20280.interfaces.Entry;
import project20280.tree.AVLTreeMap;

import java.util.*;

/**
 * Performance comparison: TreapMap vs AVLTreeMap vs java.util.TreeMap.
 *
 * <p>Operations measured (median of {@value #MEASURE_RUNS} timed runs):
 * <ol>
 *   <li>Insertion   – put() all n keys into a freshly-created map</li>
 *   <li>Search hit  – get() every key that was inserted</li>
 *   <li>Search miss – get() keys that were never inserted</li>
 *   <li>Traversal   – iterate entrySet() of a pre-built map</li>
 *   <li>Deletion    – remove() every key from a pre-built map</li>
 * </ol>
 *
 * <p>Input patterns: Random, Sorted (asc), Sorted (desc), Partially-sorted.
 * <p>Sizes n ∈ {100, 500, 1 000, 2 000, 5 000, 10 000}.
 */
public class PerformanceBenchmark {

    // Configuration
    private static final int[] SIZES        = {100, 500, 1_000, 2_000, 5_000, 10_000};
    private static final int   WARMUP_RUNS  = 3;
    private static final int   MEASURE_RUNS = 7;
    private static final long  SEED         = 0xDEADBEEFL;

    // Input patterns
    enum Pattern { RANDOM, SORTED_ASC, SORTED_DESC, PARTIAL }

    // Generates n integer keys according to the requested pattern.
    private static int[] generate(int n, Pattern p, Random rng) {
        int[] arr = new int[n];
        switch (p) {
            case SORTED_ASC:
                for (int i = 0; i < n; i++) arr[i] = i + 1;
                break;
            case SORTED_DESC:
                for (int i = 0; i < n; i++) arr[i] = n - i;
                break;
            case PARTIAL: {
                // 80 % sorted ascending, 20 % uniformly random
                int sortedPart = (int) (0.8 * n);
                for (int i = 0; i < sortedPart; i++)  arr[i] = i + 1;
                for (int i = sortedPart; i < n; i++)  arr[i] = rng.nextInt(n * 2) + 1;
                break;
            }
            default: // RANDOM
                for (int i = 0; i < n; i++) arr[i] = rng.nextInt(n * 10) + 1;
        }
        return arr;
    }

    // Map-factory lambdas (one per structure)
    // Common operations used by the benchmark, independent of map type.
    interface MapOps {
        void   insert(int k);
        Object lookup(int k);
        void   delete(int k);
        int    traverse();           // iterates all; returns count to prevent elim
        String name();
    }

    private static MapOps treap() {
        return new MapOps() {
            final TreapMap<Integer, Integer> m = new TreapMap<>();
            public void   insert(int k) { m.put(k, k); }
            public Object lookup(int k) { return m.get(k); }
            public void   delete(int k) { m.remove(k); }
            public int traverse() {
                int c = 0; for (Entry<Integer,Integer> ignored : m.entrySet()) c++; return c;
            }
            public String name() { return "TreapMap"; }
        };
    }

    private static MapOps avl() {
        return new MapOps() {
            final AVLTreeMap<Integer, Integer> m = new AVLTreeMap<>();
            public void   insert(int k) { m.put(k, k); }
            public Object lookup(int k) { return m.get(k); }
            public void   delete(int k) { m.remove(k); }
            public int traverse() {
                int c = 0; for (Entry<Integer,Integer> ignored : m.entrySet()) c++; return c;
            }
            public String name() { return "AVLTreeMap"; }
        };
    }

    private static MapOps jdk() {
        return new MapOps() {
            final java.util.TreeMap<Integer, Integer> m = new java.util.TreeMap<>();
            public void   insert(int k) { m.put(k, k); }
            public Object lookup(int k) { return m.get(k); }
            public void   delete(int k) { m.remove(k); }
            public int traverse() {
                int c = 0; for (Map.Entry<Integer,Integer> ignored : m.entrySet()) c++; return c;
            }
            public String name() { return "java.util.TreeMap"; }
        };
    }

    // Timing helpers                                                       //
    @FunctionalInterface interface Block { void run(); }

    // Runs {@code block} once and returns elapsed nanoseconds.
    private static long timeNs(Block block) {
        long t0 = System.nanoTime();
        block.run();
        return System.nanoTime() - t0;
    }

    // Returns the median of MEASURE_RUNS runs in microseconds.
    private static long medianUs(Block block) {
        long[] ns = new long[MEASURE_RUNS];
        for (int r = 0; r < MEASURE_RUNS; r++) ns[r] = timeNs(block);
        Arrays.sort(ns);
        return ns[MEASURE_RUNS / 2] / 1_000;
    }

    // Benchmark for one (structure × size × pattern) cell
    /**
     * Returns [insertUs, searchHitUs, searchMissUs, traverseUs, deleteUs].
     * Each cell is the median over MEASURE_RUNS independent runs.
     * The insert/delete cells include map-construction overhead; search and
     * traversal operate on a pre-built map shared across MEASURE_RUNS.
     */
    private static long[] benchmark(java.util.function.Supplier<MapOps> factory,
                                    int[] keys, int[] uniqueKeys) {

        //warm-up
        for (int w = 0; w < WARMUP_RUNS; w++) {
            MapOps wm = factory.get();
            for (int k : keys)       wm.insert(k);
            for (int k : uniqueKeys) wm.lookup(k);
            for (int k : uniqueKeys) wm.delete(k);
        }

        //0: insertion --------
        long tInsert = medianUs(() -> {
            MapOps m = factory.get();           // fresh map each run
            for (int k : keys) m.insert(k);
        });

        //build a stable map for search / traversal / delete
        MapOps stable = factory.get();
        for (int k : uniqueKeys) stable.insert(k);

        //1: successful search -
        long tSearchHit = medianUs(() -> {
            for (int k : uniqueKeys) stable.lookup(k);
        });

        //2: unsuccessful search
        long tSearchMiss = medianUs(() -> {
            for (int k : uniqueKeys) stable.lookup(k + 2_000_000); // guaranteed absent
        });

        //3: in-order traversal
        long tTraverse = medianUs(() -> {
            int sink = stable.traverse();       // prevent dead-code elimination
            if (sink < 0) throw new IllegalStateException(); // always false
        });

        //4: deletion (rebuild each run so every run is independent)
        long tDelete = medianUs(() -> {
            MapOps dm = factory.get();
            for (int k : uniqueKeys) dm.insert(k);
            for (int k : uniqueKeys) dm.delete(k);
        });

        return new long[]{ tInsert, tSearchHit, tSearchMiss, tTraverse, tDelete };
    }

    // Main entry point
    public static void main(String[] args) {
        final String HR  = "=".repeat(110);
        final String hr  = "─".repeat(110);

        System.out.println(HR);
        System.out.println("  Performance Benchmark: TreapMap  vs  AVLTreeMap  vs  java.util.TreeMap");
        System.out.printf ("  Median of %d runs per cell.  Times in µs.%n", MEASURE_RUNS);
        System.out.println(HR);

        Random rng = new Random(SEED);

        // Column header (printed once per pattern block)
        String header = String.format("  %-20s %6s %10s %12s %13s %11s %10s",
            "Structure", "n", "Insert", "Search(hit)", "Search(miss)", "Traversal", "Delete");

        for (Pattern pattern : Pattern.values()) {
            System.out.println();
            System.out.println(hr);
            System.out.printf("  Pattern: %s%n", pattern);
            System.out.println(hr);
            System.out.println(header);
            System.out.println("  " + "─".repeat(104));

            for (int n : SIZES) {
                int[] keys = generate(n, pattern, rng);

                // Deduplicate for search / delete (random patterns may have dupes)
                Set<Integer> seen = new LinkedHashSet<>();
                for (int k : keys) seen.add(k);
                int[] unique = seen.stream().mapToInt(x -> x).toArray();

                // Benchmark each structure
                List<java.util.function.Supplier<MapOps>> factories = new ArrayList<>();
                factories.add(PerformanceBenchmark::treap);
                factories.add(PerformanceBenchmark::avl);
                factories.add(PerformanceBenchmark::jdk);
                for (java.util.function.Supplier<MapOps> factory : factories) {
                    String name = factory.get().name();
                    long[] t = benchmark(factory, keys, unique);
                    System.out.printf("  %-20s %6d %10d %12d %13d %11d %10d%n",
                        name, n, t[0], t[1], t[2], t[3], t[4]);
                }
                System.out.println();
            }
        }

        System.out.println(HR);
        System.out.println("Benchmark complete.");
    }
}
