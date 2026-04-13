package project20280.exercises;

import project20280.interfaces.Entry;
import project20280.interfaces.Position;
import project20280.tree.TreeMap;
import project20280.tree.AVLTreeMap;

import java.util.*;


public class WK9 {


    // Q2 – Random BST construction and inorder verification


    public static void runQ2(int n, int n_max) {
        System.out.println("=== Q2: Random BST – inorder traversal ===");
        System.out.printf("  n=%d, n_max=%d%n", n, n_max);

        TreeMap<Integer, Integer> bst = new TreeMap<>();
        Random rnd = new Random();

        // Exactly the stream from the exercise sheet
        rnd.ints(1, n_max)
                .distinct()
                .limit(n)
                .boxed()
                .forEach(x -> bst.put(x, x));

        System.out.println("  Tree structure:");
        System.out.println(bst.toString());

        // Collect inorder traversal via entrySet() (which does an inorder walk)
        List<Integer> inorder = new ArrayList<>();
        for (Entry<Integer, Integer> e : bst.entrySet()) {
            inorder.add(e.getKey());
        }
        System.out.println("  Inorder: " + inorder);

        // Verify sorted order
        boolean sorted = true;
        for (int i = 1; i < inorder.size(); i++) {
            if (inorder.get(i) <= inorder.get(i - 1)) {
                sorted = false;
                break;
            }
        }
        System.out.println("  Sorted?  " + (sorted ? "YES ✓" : "NO ✗"));
        System.out.printf("  Size=%d, Height=%d%n%n",
                bst.size(), bstHeight(bst));
    }


    // Q3 – Height comparison: BST vs AVL under random put/remove



    public static void runQ3(int n_max, int n_start, int n_trials, int sampleEvery) {
        System.out.println("=== Q3: Height analysis – BST vs AVL ===");
        System.out.printf("  n_max=%d, n_start=%d, n_trials=%d%n", n_max, n_start, n_trials);

        TreeMap<Integer, Integer>    bst = new TreeMap<>();
        AVLTreeMap<Integer, Integer> avl = new AVLTreeMap<>();
        Random rnd = new Random(42); // fixed seed for reproducibility

        // Pre-load n_start distinct keys into both trees
        List<Integer> pool = new ArrayList<>();
        for (int i = 1; i < n_max; i++) pool.add(i);
        Collections.shuffle(pool, rnd);
        for (int i = 0; i < Math.min(n_start, pool.size()); i++) {
            int k = pool.get(i);
            bst.put(k, k);
            avl.put(k, k);
        }

        List<Integer> bstHeights = new ArrayList<>();
        List<Integer> avlHeights = new ArrayList<>();

        // --- Trial loop (verbatim logic from exercise sheet) ---
        for (int i = 0; i < n_trials; i++) {

            // Snapshot current key sets
            List<Integer> bstKeys = new ArrayList<>();
            bst.keySet().forEach(bstKeys::add);

            List<Integer> avlKeys = new ArrayList<>();
            avl.keySet().forEach(avlKeys::add);

            // --- BST: put or remove ---
            if (bst.size() < n_max && rnd.nextFloat() > 0.5f) {
                while (true) {
                    int x = rnd.nextInt(n_max - 1) + 1;
                    if (!bstKeys.contains(x)) { bst.put(x, x); break; }
                }
            } else if (bst.size() > 0) {
                int x = bstKeys.get(rnd.nextInt(bstKeys.size()));
                bst.remove(x);
            }

            // --- AVL: independent put or remove ---
            if (avl.size() < n_max && rnd.nextFloat() > 0.5f) {
                while (true) {
                    int x = rnd.nextInt(n_max - 1) + 1;
                    if (!avlKeys.contains(x)) { avl.put(x, x); break; }
                }
            } else if (avl.size() > 0) {
                int x = avlKeys.get(rnd.nextInt(avlKeys.size()));
                avl.remove(x);
            }

            // Sample heights every sampleEvery steps
            if (i % sampleEvery == 0) {
                bstHeights.add(bstHeight(bst));
                avlHeights.add(avlHeight(avl));
            }
        }

        // --- Statistics ---
        double bstAvg = average(bstHeights);
        double avlAvg = average(avlHeights);
        double sqrtN  = Math.sqrt(n_max);
        double logN   = Math.log(n_max) / Math.log(2);

        System.out.printf("  BST avg height : %.2f%n", bstAvg);
        System.out.printf("  AVL avg height : %.2f%n", avlAvg);
        System.out.printf("  sqrt(n_max)    : %.2f  (expected BST scaling)%n", sqrtN);
        System.out.printf("  log2(n_max)    : %.2f  (expected AVL scaling)%n", logN);
        System.out.printf("  BST / AVL ratio: %.2f×%n", bstAvg / avlAvg);

        printAsciiHeightChart(bstHeights, avlHeights, 40);
        System.out.println();
    }


    public static void runQ3(int n_max) {
        runQ3(n_max, n_max / 2, 10_000, 50);
    }


    // Q4 – Tree-sort performance comparison
    public static void runQ4(int[] sizes, int runs) {
        System.out.println("=== Q4: Tree-sort performance comparison ===");
        System.out.printf("  Runs per size: %d%n%n", runs);

        System.out.printf("  %-8s  %-16s  %-16s  %-16s  %-8s%n",
                "n", "TreeMap (ms)", "AVLTreeMap (ms)", "java.util (ms)", "Correct?");
        System.out.println("  " + "-".repeat(70));

        Random rnd = new Random(99);

        for (int n : sizes) {
            long bstTotal = 0, avlTotal = 0, jdkTotal = 0;
            boolean allCorrect = true;

            for (int r = 0; r < runs; r++) {
                int[] raw = rnd.ints(n, 0, n * 3).toArray();

                // 1. project20280.tree.TreeMap (plain BST)
                long t0 = System.nanoTime();
                List<Integer> bstSorted = treeMapSort(raw);
                bstTotal += System.nanoTime() - t0;

                // 2. project20280.tree.AVLTreeMap
                t0 = System.nanoTime();
                List<Integer> avlSorted = avlTreeMapSort(raw);
                avlTotal += System.nanoTime() - t0;

                // 3. java.util.TreeMap (Red-Black tree, fully qualified)
                t0 = System.nanoTime();
                List<Integer> jdkSorted = jdkTreeMapSort(raw);
                jdkTotal += System.nanoTime() - t0;

                if (!bstSorted.equals(avlSorted) || !avlSorted.equals(jdkSorted))
                    allCorrect = false;
            }

            System.out.printf("  %-8d  %-16.3f  %-16.3f  %-16.3f  %-8s%n",
                    n,
                    bstTotal / 1_000_000.0 / runs,
                    avlTotal / 1_000_000.0 / runs,
                    jdkTotal / 1_000_000.0 / runs,
                    allCorrect ? "YES ✓" : "NO ✗");
        }

        System.out.println();
        System.out.println("  Notes:");
        System.out.println("  - TreeMap    = plain BST  – O(n log n) avg, O(n²) worst (sorted input)");
        System.out.println("  - AVLTreeMap = balanced   – O(n log n) always, rotation overhead visible");
        System.out.println("  - java.util  = Red-Black  – O(n log n) always, JIT-optimised in practice");
        System.out.println("  - Duplicates are collapsed (map semantics: last value wins)");
        System.out.println();
    }

    /** Convenience overload: benchmarks a default range of sizes. */
    public static void runQ4() {
        runQ4(new int[]{100, 500, 1_000, 5_000, 10_000, 50_000}, 10);
    }


    // Tree-sort implementations


    /** Tree-sort using project20280.tree.TreeMap (plain BST). */
    private static List<Integer> treeMapSort(int[] arr) {
        TreeMap<Integer, Integer> map = new TreeMap<>();
        for (int x : arr) map.put(x, x);
        List<Integer> out = new ArrayList<>(map.size());
        for (Entry<Integer, Integer> e : map.entrySet()) out.add(e.getKey());
        return out;
    }

    /** Tree-sort using project20280.tree.AVLTreeMap. */
    private static List<Integer> avlTreeMapSort(int[] arr) {
        AVLTreeMap<Integer, Integer> map = new AVLTreeMap<>();
        for (int x : arr) map.put(x, x);
        List<Integer> out = new ArrayList<>(map.size());
        for (Entry<Integer, Integer> e : map.entrySet()) out.add(e.getKey());
        return out;
    }

    //Tree-sort using java.util.TreeMap (Red-Black tree, fully qualified)
    private static List<Integer> jdkTreeMapSort(int[] arr) {
        java.util.TreeMap<Integer, Integer> map = new java.util.TreeMap<>();
        for (int x : arr) map.put(x, x);
        return new ArrayList<>(map.keySet()); // keySet() is already sorted
    }


    // Height helpers – two explicit methods, one per concrete type,
    // so there is zero ambiguity between your TreeMap and java.util.TreeMap.



    private static int bstHeight(TreeMap<Integer, Integer> map) {
        if (map.isEmpty()) return 0;
        return posHeight(map, map.root());
    }


    private static int posHeight(TreeMap<Integer, Integer> map,
                                 Position<Entry<Integer, Integer>> p) {
        if (map.isExternal(p)) return 0;
        return 1 + Math.max(posHeight(map, map.left(p)),
                posHeight(map, map.right(p)));
    }


    private static int avlHeight(AVLTreeMap<Integer, Integer> map) {
        if (map.isEmpty()) return 0;
        return map.height(map.root()); // reads aux field, O(1)
    }


    // General helpers


    private static double average(List<Integer> vals) {
        if (vals.isEmpty()) return 0;
        long sum = 0;
        for (int v : vals) sum += v;
        return (double) sum / vals.size();
    }

    private static void printAsciiHeightChart(List<Integer> bst,
                                              List<Integer> avl,
                                              int cols) {
        int rows = 10;
        int maxH = 0;
        for (int v : bst) maxH = Math.max(maxH, v);
        for (int v : avl) maxH = Math.max(maxH, v);
        if (maxH == 0) return;

        int step = Math.max(1, bst.size() / cols);
        List<Integer> bstS = new ArrayList<>(), avlS = new ArrayList<>();
        for (int i = 0; i < bst.size(); i += step) {
            bstS.add(bst.get(i));
            avlS.add(avl.get(i));
        }

        System.out.println("  Height over time  (B=BST only, A=AVL only, *=both):");
        for (int row = rows; row >= 1; row--) {
            int thr = (int) Math.round((double) row / rows * maxH);
            StringBuilder sb = new StringBuilder();
            for (int c = 0; c < bstS.size(); c++) {
                boolean b = bstS.get(c) >= thr;
                boolean a = avlS.get(c) >= thr;
                sb.append(b && a ? '*' : b ? 'B' : a ? 'A' : ' ');
            }
            System.out.printf("  %3d | %s%n", thr, sb);
        }
        System.out.println("      +" + "-".repeat(bstS.size() + 1));
        System.out.println("       trial progression →");
    }





    public static void main(String[] args) {

        // Q2 – two sizes as on the sheet
        runQ2(20, 50);
        runQ2(100, 200);

        // Q3 – sweep n_max to reveal the O(sqrt n) vs O(log n) trend
        for (int nmax : new int[]{50, 100, 500, 1000}) {
            runQ3(nmax);
        }

        // Q4 – full benchmark table
        runQ4();
    }
}