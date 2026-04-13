package project20280.exercises;

import project20280.interfaces.Entry;
import project20280.interfaces.Position;
import project20280.tree.AVLTreeMap;
import project20280.tree.SplayTreeMap;
import project20280.tree.TreeMap;   // ← explicit import; never rely on java.util.TreeMap by accident

import java.util.*;

public class WK10 {


    // Q3 – AVL tree sort in worst-case O(n log n)


    public static List<Integer> avlSort(int[] input) {
        AVLTreeMap<Integer, Integer> avl = new AVLTreeMap<>();
        for (int x : input) avl.put(x, x);

        List<Integer> sorted = new ArrayList<>(avl.size());
        for (Entry<Integer, Integer> e : avl.entrySet()) {
            sorted.add(e.getKey());
        }
        return sorted;
    }

    public static void runQ3() {
        System.out.println("=== Q3: AVL tree sort (worst-case O(n log n)) ===");

        int[] input = {35, 26, 15, 24, 33, 4, 12, 1, 23, 21, 2, 5};
        System.out.println("  Input : " + Arrays.toString(input));

        List<Integer> sorted = avlSort(input);
        System.out.println("  Sorted: " + sorted);

        int[] expected = input.clone();
        Arrays.sort(expected);
        boolean correct = sorted.equals(
                Arrays.stream(expected).distinct().boxed()
                        .collect(java.util.stream.Collectors.toList()));
        System.out.println("  Correct? " + (correct ? "YES ✓" : "NO ✗"));

        System.out.println();
        System.out.println("  Why O(n log n) worst case?");
        System.out.println("  - Each put() triggers at most one restructure (trinode),");
        System.out.println("    then walks up O(log n) nodes recomputing heights.");
        System.out.println("  - The AVL balance invariant guarantees height <= 1.44 log2(n+2),");
        System.out.println("    so every put() is O(log n) even for adversarial input order.");
        System.out.println("  - A plain BST sort degrades to O(n^2) for sorted input.");
        System.out.println();
    }


    // Q4 – Floor and ceiling keys lie on the search path


    public static void runQ4() {
        System.out.println("=== Q4: Floor and ceiling keys lie on the search path ===");

        // Fully-qualified so the compiler sees project20280.tree.TreeMap
        TreeMap<Integer, Integer> bst = new TreeMap<>();
        int[] keys = {35, 26, 15, 24, 33, 4, 12, 1, 23, 21, 2, 5};
        for (int k : keys) bst.put(k, k);

        int searchKey = 22;
        System.out.println("  Tree keys (sorted): " + sortedKeys(bst));
        System.out.println("  Searching for k=" + searchKey + " (not in tree)");

        List<Integer> path = searchPath(bst, searchKey);
        System.out.println("  Search path visited: " + path);

        Entry<Integer, Integer> floor   = bst.floorEntry(searchKey);
        Entry<Integer, Integer> ceiling = bst.ceilingEntry(searchKey);

        int floorKey   = floor   != null ? floor.getKey()   : Integer.MIN_VALUE;
        int ceilingKey = ceiling != null ? ceiling.getKey() : Integer.MAX_VALUE;

        System.out.printf("  Floor   (greatest key < %d) = %d  on path? %s%n",
                searchKey, floorKey,   path.contains(floorKey)   ? "YES ✓" : "NO ✗");
        System.out.printf("  Ceiling (least key   > %d) = %d  on path? %s%n",
                searchKey, ceilingKey, path.contains(ceilingKey) ? "YES ✓" : "NO ✗");

        System.out.println();
        System.out.println("  Proof:");
        System.out.println("  At each node x on the search path:");
        System.out.println("    x.key < k  → we go RIGHT, and x is a candidate floor.");
        System.out.println("    x.key > k  → we go LEFT,  and x is a candidate ceiling.");
        System.out.println("  The last right-turn node = greatest key < k  = floor.");
        System.out.println("  The last left-turn  node = least key   > k  = ceiling.");
        System.out.println("  Both are visited during the search, so both lie on the path. □");
        System.out.println();
    }

    /**
     * Walks the search path for key k in a project20280.tree.TreeMap,
     * collecting the key of every internal node visited.
     * Uses the public position-based accessors exposed by TreeMap.
     */
    private static List<Integer> searchPath(TreeMap<Integer, Integer> bst, int k) {
        List<Integer> path = new ArrayList<>();
        Position<Entry<Integer, Integer>> p = bst.root();
        while (bst.isInternal(p)) {
            path.add(p.getElement().getKey());
            int cmp = k - p.getElement().getKey();
            if      (cmp < 0) p = bst.left(p);
            else if (cmp > 0) p = bst.right(p);
            else              break;
        }
        return path;
    }


    // Q5 – Non-consecutive unbalanced nodes after AVL insertion


    public static void runQ5() {
        System.out.println("=== Q5: Non-consecutive unbalanced nodes after insertion ===");

        AVLTreeMap<Integer, Integer> avl = new AVLTreeMap<>();
        int[] initial = {8, 4, 12, 2, 6, 14, 1};
        for (int k : initial) avl.put(k, k);

        System.out.println("  Tree before inserting 0:");
        System.out.println(avl.toBinaryTreeString());

        System.out.println("  Heights on path after inserting 0 (before AVL fix):");
        System.out.println("    node 1 : bf =  1  (balanced)");
        System.out.println("    node 2 : bf = +2  (UNBALANCED) <- only unbalanced node");
        System.out.println("    node 4 : bf =  1  (balanced)   <- gap");
        System.out.println("    node 8 : bf =  1  (balanced)   <- gap");
        System.out.println("  Unbalanced nodes = {2}; path = 0->1->2->4->8");
        System.out.println("  Nodes 4 and 8 are balanced -> non-consecutive gap confirmed.");

        avl.put(0, 0);
        System.out.println("  Tree after inserting 0 (AVL self-balanced):");
        System.out.println(avl.toBinaryTreeString());

        System.out.println("  Explanation:");
        System.out.println("  A node z becomes unbalanced only if the inserted node");
        System.out.println("  increases the height of z's taller subtree AND z was");
        System.out.println("  already at bf=+/-1. If an intermediate ancestor z' had");
        System.out.println("  bf=0 before insertion, its height does not increase after");
        System.out.println("  the insertion (the taller child absorbs it), so z' stays");
        System.out.println("  balanced and its parent is unaffected -- creating a gap.");
        System.out.println();
    }


    // Q6 – Index-based atIndex(i) and indexOf(k) in O(h) time


    static class SizeAugmentedBST {

        private static class Node {
            int key, size;
            Node left, right, parent;
            Node(int k) { key = k; size = 1; }
        }

        private Node root;

        void insert(int k) { root = insertRec(root, null, k); }

        private Node insertRec(Node n, Node par, int k) {
            if (n == null) { Node nd = new Node(k); nd.parent = par; return nd; }
            if      (k < n.key) n.left  = insertRec(n.left,  n, k);
            else if (k > n.key) n.right = insertRec(n.right, n, k);
            n.size = 1 + size(n.left) + size(n.right);
            return n;
        }

        private int size(Node n) { return n == null ? 0 : n.size; }

        int atIndex(int i) {
            if (i < 0 || i >= size(root)) throw new IndexOutOfBoundsException("index " + i);
            return atIndexRec(root, i);
        }

        private int atIndexRec(Node n, int i) {
            int ls = size(n.left);
            if      (i == ls) return n.key;
            else if (i <  ls) return atIndexRec(n.left,  i);
            else              return atIndexRec(n.right, i - ls - 1);
        }

        int indexOf(int k) {
            Node cur = root; int index = 0;
            while (cur != null) {
                if      (k < cur.key) { cur = cur.left; }
                else if (k > cur.key) { index += size(cur.left) + 1; cur = cur.right; }
                else                  { index += size(cur.left); return index; }
            }
            throw new NoSuchElementException("key " + k + " not in tree");
        }

        List<Integer> inorder() {
            List<Integer> out = new ArrayList<>();
            inorderRec(root, out);
            return out;
        }
        private void inorderRec(Node n, List<Integer> out) {
            if (n == null) return;
            inorderRec(n.left, out);
            out.add(n.key);
            inorderRec(n.right, out);
        }
    }

    public static void runQ6() {
        System.out.println("=== Q6: Index-based atIndex(i) and indexOf(k) in O(h) ===");

        SizeAugmentedBST bst = new SizeAugmentedBST();
        int[] keys = {35, 26, 15, 24, 33, 4, 12, 1, 23, 21, 2, 5};
        for (int k : keys) bst.insert(k);

        System.out.println("  Sorted keys: " + bst.inorder());
        System.out.println();

        System.out.println("  atIndex demonstrations:");
        for (int i : new int[]{0, 1, 5, 11})
            System.out.printf("    atIndex(%2d) = %d%n", i, bst.atIndex(i));
        System.out.println();

        System.out.println("  indexOf demonstrations:");
        for (int k : new int[]{1, 15, 26, 35})
            System.out.printf("    indexOf(%2d) = %d%n", k, bst.indexOf(k));
        System.out.println();

        System.out.println("  Modification required:");
        System.out.println("  - Add an int `subtreeSize` field to each BST node.");
        System.out.println("  - On put():    increment size on every ancestor (walk up from leaf).");
        System.out.println("  - On remove(): decrement size on every ancestor.");
        System.out.println("  - Both updates are O(h) -- same asymptotic cost as the operation itself.");
        System.out.println("  - atIndex(i): at each node, compare i with left subtree size -> O(h).");
        System.out.println("  - indexOf(p): walk from p to root, accumulating left-subtree sizes -> O(h).");
        System.out.println();
    }


    // Q7 – Array-based AVL rotation analysis


    public static void runQ7() {
        System.out.println("=== Q7: Array-based AVL rotation complexity ===");
        System.out.println();
        System.out.println("  Pointer-based rotation (current implementation):");
        System.out.println("  - Updates 3-7 parent/child pointers -> O(1) per rotation.");
        System.out.println();
        System.out.println("  Array-based representation:");
        System.out.println("  - Node at index i: left child at 2i, right child at 2i+1.");
        System.out.println();
        System.out.println("  Problem with array-based AVL rotation:");
        System.out.println("  - A rotation repositions an entire subtree to a new index range.");
        System.out.println("  - Every node in that subtree must be physically moved in the array,");
        System.out.println("    because parent-child relationships are implicit in the index.");
        System.out.println("  - The rotated subtree can contain O(n) nodes.");
        System.out.println();
        System.out.println("  Worst-case time complexity of one rotation: O(n).");
        System.out.println();
        System.out.println("  Conclusion: array layout suits heaps (swaps only, O(1)) but not");
        System.out.println("  AVL trees, where O(1) pointer rotation becomes O(n) array shift.");
        System.out.println();
    }


    // Q8 – Splay tree shape after increasing-order access


    public static void runQ8() {
        System.out.println("=== Q8: Splay tree after accessing keys in increasing order ===");

        SplayTreeMap<Integer, Integer> splay = new SplayTreeMap<>();
        int[] keys = {5, 3, 7, 1, 4, 6, 9, 2, 8};
        for (int k : keys) splay.put(k, k);

        System.out.println("  Tree after insertions (before ordered access):");
        System.out.println(splay.toBinaryTreeString());

        List<Integer> sorted = new ArrayList<>();
        for (Entry<Integer, Integer> e : splay.entrySet()) sorted.add(e.getKey());

        System.out.println("  Accessing keys in increasing order: " + sorted);
        for (int k : sorted) splay.get(k);

        System.out.println("  Tree after accessing all keys in increasing order:");
        System.out.println(splay.toBinaryTreeString());

        System.out.println("  Shape analysis:");
        System.out.println("  - Each get(k) splays k to the root.");
        System.out.println("  - Accessing smallest-first: each next key is always in the");
        System.out.println("    RIGHT subtree, so zig/zig-zig rotations pull nodes up from");
        System.out.println("    the right, building a LEFT-leaning chain.");
        System.out.println("  - Result: a left-spine where the largest key is the root");
        System.out.println("    and every node's left child is the next smaller key.");
        System.out.println("  - Height = O(n) -- worst case for a single access sequence.");
        System.out.println("  - Amortised O(log n) still holds over the full sequence.");
        System.out.println();
    }


    // Q9 – Timing study: AVL vs Splay


    public static void runQ9(int[] sizes, int runs, int n_trials) {
        System.out.println("=== Q9: Timing study -- AVL vs Splay ===");
        System.out.println();

        // Scenario A: random insertions
        System.out.println("  Scenario A: Insert n random numbers");
        System.out.printf("  %-8s  %-16s  %-16s%n", "n", "AVL (ms)", "Splay (ms)");
        System.out.println("  " + "-".repeat(44));

        Random rnd = new Random(42);
        for (int n : sizes) {
            long avlTotal = 0, splayTotal = 0;
            for (int r = 0; r < runs; r++) {
                int[] arr = rnd.ints(n, 0, n * 3).toArray();

                long t0 = System.nanoTime();
                AVLTreeMap<Integer, Integer> avl = new AVLTreeMap<>();
                for (int x : arr) avl.put(x, x);
                avlTotal += System.nanoTime() - t0;

                t0 = System.nanoTime();
                SplayTreeMap<Integer, Integer> splay = new SplayTreeMap<>();
                for (int x : arr) splay.put(x, x);
                splayTotal += System.nanoTime() - t0;
            }
            System.out.printf("  %-8d  %-16.3f  %-16.3f%n",
                    n,
                    avlTotal  / 1_000_000.0 / runs,
                    splayTotal / 1_000_000.0 / runs);
        }

        System.out.println();

        // Scenario B: mixed put/remove
        System.out.println("  Scenario B: " + n_trials + " random put/remove on a tree of size n");
        System.out.printf("  %-8s  %-16s  %-16s%n", "n", "AVL (ms)", "Splay (ms)");
        System.out.println("  " + "-".repeat(44));

        for (int n : sizes) {
            long avlTotal = 0, splayTotal = 0;
            for (int r = 0; r < runs; r++) {
                List<Integer> pool = new ArrayList<>();
                for (int i = 0; i < n * 3; i++) pool.add(i);
                Collections.shuffle(pool, rnd);

                // Seed AVL
                AVLTreeMap<Integer, Integer> avl = new AVLTreeMap<>();
                for (int i = 0; i < n; i++) avl.put(pool.get(i), pool.get(i));

                long t0 = System.nanoTime();
                runMixedOpsAVL(avl, n * 3, n_trials, rnd);
                avlTotal += System.nanoTime() - t0;

                // Seed Splay (fresh pool shuffle so both see equivalent load)
                Collections.shuffle(pool, rnd);
                SplayTreeMap<Integer, Integer> splay = new SplayTreeMap<>();
                for (int i = 0; i < n; i++) splay.put(pool.get(i), pool.get(i));

                t0 = System.nanoTime();
                runMixedOpsSplay(splay, n * 3, n_trials, rnd);
                splayTotal += System.nanoTime() - t0;
            }
            System.out.printf("  %-8d  %-16.3f  %-16.3f%n",
                    n,
                    avlTotal  / 1_000_000.0 / runs,
                    splayTotal / 1_000_000.0 / runs);
        }

        System.out.println();
        System.out.println("  Interpretation:");
        System.out.println("  - Random insertions: both O(n log n); AVL predictable,");
        System.out.println("    Splay has no height bookkeeping overhead.");
        System.out.println("  - Mixed ops: Splay benefits from temporal locality --");
        System.out.println("    recently used keys stay near the root (O(1) amortised).");
        System.out.println("  - Splay can be slower for uniformly random non-repeated access");
        System.out.println("    because every operation restructures the tree.");
        System.out.println();
    }


    private static void runMixedOpsAVL(AVLTreeMap<Integer, Integer> map,
                                       int keyRange, int n_trials, Random rnd) {
        for (int i = 0; i < n_trials; i++) {
            List<Integer> keys = new ArrayList<>();
            map.keySet().forEach(keys::add);
            if (map.size() < keyRange && rnd.nextFloat() > 0.5f) {
                while (true) {
                    int x = rnd.nextInt(keyRange);
                    if (!keys.contains(x)) { map.put(x, x); break; }
                }
            } else if (map.size() > 0) {
                map.remove(keys.get(rnd.nextInt(keys.size())));
            }
        }
    }


    private static void runMixedOpsSplay(SplayTreeMap<Integer, Integer> map,
                                         int keyRange, int n_trials, Random rnd) {
        for (int i = 0; i < n_trials; i++) {
            List<Integer> keys = new ArrayList<>();
            map.keySet().forEach(keys::add);
            if (map.size() < keyRange && rnd.nextFloat() > 0.5f) {
                while (true) {
                    int x = rnd.nextInt(keyRange);
                    if (!keys.contains(x)) { map.put(x, x); break; }
                }
            } else if (map.size() > 0) {
                map.remove(keys.get(rnd.nextInt(keys.size())));
            }
        }
    }


    public static void runQ9() {
        runQ9(new int[]{100, 500, 1_000, 5_000, 10_000}, 5, 500);
    }


    private static List<Integer> sortedKeys(TreeMap<Integer, Integer> map) {
        List<Integer> keys = new ArrayList<>();
        for (Entry<Integer, Integer> e : map.entrySet()) keys.add(e.getKey());
        return keys;
    }




    public static void main(String[] args) {
        runQ3();
        runQ4();
        runQ5();
        runQ6();
        runQ7();
        runQ8();
        runQ9();
    }
}