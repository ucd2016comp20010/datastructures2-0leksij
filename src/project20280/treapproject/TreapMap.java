package project20280.treapproject;

import project20280.interfaces.Entry;
import project20280.tree.AbstractSortedMap;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * A Treap (Tree + Heap) implementation of the SortedMap ADT.
 *
 * <p>Each node stores a key (satisfying BST property), a value, and a random
 * integer priority (satisfying max-heap property).  Together these two
 * invariants give expected O(log n) height and therefore O(log n) expected
 * cost for get, put, and remove.</p>
 *
 * <p>Based on the TreeMap / AVLTreeMap lecture framework; shares the same ADT
 * (AbstractSortedMap) but uses its own node class and rotation strategy so that
 * the unusual pre-deletion rotation required by Treap delete can be expressed
 * cleanly.</p>
 *
 *
 */
public class TreapMap<K, V> extends AbstractSortedMap<K, V> {


    // Inner node class


    //A node in the Treap; also acts as an Entry<K,V> to support entrySet().
    private static class TreapNode<K, V> implements Entry<K, V> {
        K key;
        V value;
        int priority;                // random; max-heap property
        TreapNode<K, V> parent;
        TreapNode<K, V> left;
        TreapNode<K, V> right;

        TreapNode(K key, V value, int priority, TreapNode<K, V> parent) {
            this.key      = key;
            this.value    = value;
            this.priority = priority;
            this.parent   = parent;
        }

        @Override public K getKey()   { return key; }
        @Override public V getValue() { return value; }

        V setValue(V v) {
            V old = value;
            value = v;
            return old;
        }

        @Override
        public String toString() {
            return "<" + key + ", p=" + priority + ">";
        }
    }


    // Fields
    private final Random rnd = new Random();
    private TreapNode<K, V> root = null;
    private int size = 0;


    // Constructors


    /** Constructs an empty Treap using natural key ordering. */
    public TreapMap() { super(); }

    /** Constructs an empty Treap using the given comparator. */
    public TreapMap(Comparator<K> comp) { super(comp); }

    /** Constructs an empty Treap with a fixed random seed (useful for tests). */
    public TreapMap(long seed) {
        super();
        rnd.setSeed(seed);
    }


    // Size


    @Override
    public int size() { return size; }


    // Rotation helpers
    /**
     * Right-rotation: rotates the subtree rooted at {@code y} to the right,
     * making {@code y.left} the new subtree root.
     *
     * <pre>
     *      y              x
     *     / \            / \
     *    x   t2   →    t0   y
     *   / \                / \
     *  t0  t1             t1  t2
     * </pre>
     */
    private void rotateRight(TreapNode<K, V> y) {
        TreapNode<K, V> x  = y.left;
        TreapNode<K, V> t1 = x.right;

        // x replaces y in the tree
        x.parent = y.parent;
        if (y.parent == null) {
            root = x;
        } else if (y == y.parent.left) {
            y.parent.left  = x;
        } else {
            y.parent.right = x;
        }

        // rewire
        x.right  = y;  y.parent = x;
        y.left   = t1; if (t1 != null) t1.parent = y;
    }

    /**
     * Left-rotation: rotates the subtree rooted at {@code x} to the left,
     * making {@code x.right} the new subtree root.
     *
     * <pre>
     *    x                y
     *   / \              / \
     *  t0   y    →     x   t2
     *      / \        / \
     *     t1  t2     t0  t1
     * </pre>
     */
    private void rotateLeft(TreapNode<K, V> x) {
        TreapNode<K, V> y  = x.right;
        TreapNode<K, V> t1 = y.left;

        // y replaces x in the tree
        y.parent = x.parent;
        if (x.parent == null) {
            root = y;
        } else if (x == x.parent.left) {
            x.parent.left  = y;
        } else {
            x.parent.right = y;
        }

        // rewire
        y.left   = x;  x.parent = y;
        x.right  = t1; if (t1 != null) t1.parent = x;
    }


    // Internal BST search


    /** Returns the node with the given key, or {@code null} if absent. */
    private TreapNode<K, V> findNode(K key) {
        TreapNode<K, V> curr = root;
        while (curr != null) {
            int cmp = compare(key, curr.key);
            if      (cmp == 0) return curr;
            else if (cmp <  0) curr = curr.left;
            else               curr = curr.right;
        }
        return null;
    }


    // Map ADT


    /**
     * Returns the value mapped to {@code key}, or {@code null} if absent.
     * Expected O(log n).
     */
    @Override
    public V get(K key) throws IllegalArgumentException {
        TreapNode<K, V> node = findNode(key);
        return node == null ? null : node.value;
    }

    /**
     * Inserts or updates the mapping for {@code key}.
     * After a standard BST insert a "bubble-up" phase restores the
     * max-heap priority invariant via rotations.
     * Expected O(log n).
     *
     * @return the old value, or {@code null} if key was not present
     */
    @Override
    public V put(K key, V value) throws IllegalArgumentException {
        // --- BST descent to find the correct leaf position ---
        TreapNode<K, V> parent = null;
        TreapNode<K, V> curr   = root;
        boolean wentLeft = false;

        while (curr != null) {
            int cmp = compare(key, curr.key);
            if (cmp == 0) {
                // key already exists — update value only, no structural change
                return curr.setValue(value);
            }
            parent   = curr;
            wentLeft = (cmp < 0);
            curr     = wentLeft ? curr.left : curr.right;
        }

        // --- Insert new node ---
        TreapNode<K, V> node = new TreapNode<>(key, value, rnd.nextInt(), parent);
        if (parent == null) {
            root = node;          // tree was empty
        } else if (wentLeft) {
            parent.left  = node;
        } else {
            parent.right = node;
        }
        size++;

        // --- Bubble up: rotate node above its parent while heap property violated ---
        bubbleUp(node);
        return null;
    }

    /** Rotates {@code node} upward until the max-heap priority property holds. */
    private void bubbleUp(TreapNode<K, V> node) {
        while (node.parent != null && node.priority > node.parent.priority) {
            if (node == node.parent.left) {
                rotateRight(node.parent);   // node is left child  → right-rotate parent
            } else {
                rotateLeft(node.parent);    // node is right child → left-rotate parent
            }
        }
    }

    /**
     * Removes the entry with the given key (if present) and returns its value.
     * The node is first pushed down to a leaf via rotations (so heap order is
     * maintained at each step), then unlinked.
     * Expected O(log n).
     *
     * @return the removed value, or {@code null} if the key was absent
     */
    @Override
    public V remove(K key) throws IllegalArgumentException {
        TreapNode<K, V> node = findNode(key);
        if (node == null) return null;

        V old = node.value;
        pushDown(node);

        // node is now a leaf — unlink it
        if (node.parent == null) {
            root = null;
        } else if (node == node.parent.left) {
            node.parent.left  = null;
        } else {
            node.parent.right = null;
        }
        size--;
        return old;
    }

    /**
     * Pushes {@code node} down toward a leaf by repeatedly rotating up
     * whichever child has the higher priority.
     */
    private void pushDown(TreapNode<K, V> node) {
        while (node.left != null || node.right != null) {
            if (node.left == null) {
                rotateLeft(node);           // only right child exists
            } else if (node.right == null) {
                rotateRight(node);          // only left child exists
            } else if (node.left.priority > node.right.priority) {
                rotateRight(node);          // left child has higher priority → goes up
            } else {
                rotateLeft(node);           // right child has higher priority → goes up
            }
        }
    }


    // SortedMap ADT


    /** Returns the leftmost (minimum-key) node, or {@code null} if empty. */
    private TreapNode<K, V> minNode(TreapNode<K, V> node) {
        if (node == null) return null;
        while (node.left != null) node = node.left;
        return node;
    }

    /** Returns the rightmost (maximum-key) node, or {@code null} if empty. */
    private TreapNode<K, V> maxNode(TreapNode<K, V> node) {
        if (node == null) return null;
        while (node.right != null) node = node.right;
        return node;
    }

    @Override
    public Entry<K, V> firstEntry() {
        return minNode(root);
    }

    @Override
    public Entry<K, V> lastEntry() {
        return maxNode(root);
    }

    /**
     * Returns the entry with the least key ≥ {@code key}.
     */
    @Override
    public Entry<K, V> ceilingEntry(K key) throws IllegalArgumentException {
        TreapNode<K, V> result = null;
        TreapNode<K, V> curr   = root;
        while (curr != null) {
            int cmp = compare(key, curr.key);
            if (cmp == 0) return curr;          // exact match
            if (cmp < 0) {
                result = curr;  //curr is a candidate
                curr   = curr.left;
            } else {
                curr = curr.right;
            }
        }
        return result;
    }

    /**
     * Returns the entry with the greatest key ≤ {@code key}.
     */
    @Override
    public Entry<K, V> floorEntry(K key) throws IllegalArgumentException {
        TreapNode<K, V> result = null;
        TreapNode<K, V> curr   = root;
        while (curr != null) {
            int cmp = compare(key, curr.key);
            if (cmp == 0) return curr;          // exact match
            if (cmp > 0) {
                result = curr;  //curr is a candidate
                curr   = curr.right;
            } else {
                curr = curr.left;
            }
        }
        return result;
    }

    /**
     * Returns the entry with the greatest key strictly less than {@code key}.
     */
    @Override
    public Entry<K, V> lowerEntry(K key) throws IllegalArgumentException {
        TreapNode<K, V> result = null;
        TreapNode<K, V> curr   = root;
        while (curr != null) {
            if (compare(key, curr.key) > 0) {
                result = curr;
                curr   = curr.right;
            } else {
                curr = curr.left;
            }
        }
        return result;
    }

    /**
     * Returns the entry with the least key strictly greater than {@code key}.
     */
    @Override
    public Entry<K, V> higherEntry(K key) throws IllegalArgumentException {
        TreapNode<K, V> result = null;
        TreapNode<K, V> curr   = root;
        while (curr != null) {
            if (compare(key, curr.key) < 0) {
                result = curr;
                curr   = curr.left;
            } else {
                curr = curr.right;
            }
        }
        return result;
    }


    // Iteration


    /**
     * Returns all entries in sorted key order via in-order traversal.
     * O(n).
     */
    @Override
    public Iterable<Entry<K, V>> entrySet() {
        List<Entry<K, V>> result = new ArrayList<>(size);
        inorder(root, result);
        return result;
    }

    private void inorder(TreapNode<K, V> node, List<Entry<K, V>> out) {
        if (node == null) return;
        inorder(node.left,  out);
        out.add(node);
        inorder(node.right, out);
    }

    /**
     * Returns entries whose keys are in [{@code fromKey}, {@code toKey}).
     */
    @Override
    public Iterable<Entry<K, V>> subMap(K fromKey, K toKey) throws IllegalArgumentException {
        List<Entry<K, V>> result = new ArrayList<>();
        if (compare(fromKey, toKey) < 0) {
            subMapRecurse(root, fromKey, toKey, result);
        }
        return result;
    }

    private void subMapRecurse(TreapNode<K, V> node, K fromKey, K toKey,
                                List<Entry<K, V>> out) {
        if (node == null) return;
        if (compare(node.key, fromKey) < 0) {
            // node.key < fromKey — only the right subtree can contribute
            subMapRecurse(node.right, fromKey, toKey, out);
        } else {
            // node.key >= fromKey — left subtree may contribute
            subMapRecurse(node.left, fromKey, toKey, out);
            if (compare(node.key, toKey) < 0) {   // node.key in [fromKey, toKey)
                out.add(node);
                subMapRecurse(node.right, fromKey, toKey, out);
            }
        }
    }


    // Height (for diagnostics)


    /** Returns the height of the tree (0 for an empty tree). */
    public int height() {
        return heightOf(root);
    }

    private int heightOf(TreapNode<K, V> node) {
        if (node == null) return 0;
        return 1 + Math.max(heightOf(node.left), heightOf(node.right));
    }


    // Heap-property validation (for testing)


    /**
     * Returns {@code true} iff every node's priority is ≥ its children's priorities
     * (max-heap property) and the BST key invariant holds throughout.
     */
    public boolean isValid() {
        return validateNode(root, null, null);
    }

    private boolean validateNode(TreapNode<K, V> node, K minKey, K maxKey) {
        if (node == null) return true;
        // BST bounds
        if (minKey != null && compare(node.key, minKey) <= 0) return false;
        if (maxKey != null && compare(node.key, maxKey) >= 0) return false;
        // Heap property
        if (node.left  != null && node.left.priority  > node.priority) return false;
        if (node.right != null && node.right.priority > node.priority) return false;
        return validateNode(node.left,  minKey,    node.key)
            && validateNode(node.right, node.key,  maxKey);
    }


    // String output


    /** Returns a sorted list representation: [k1, k2, ...]. */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Entry<K, V> e : entrySet()) {
            if (!first) sb.append(", ");
            sb.append(e.getKey());
            first = false;
        }
        return sb.append("]").toString();
    }

    /** Returns a multi-line tree diagram for debugging. */
    public String toBinaryTreeString() {
        StringBuilder sb = new StringBuilder();
        printTree(root, sb, "", "");
        return sb.toString();
    }

    private void printTree(TreapNode<K, V> node, StringBuilder sb,
                           String prefix, String childPrefix) {
        if (node == null) { sb.append(prefix).append("(empty)\n"); return; }
        sb.append(prefix).append(node).append('\n');
        if (node.left != null || node.right != null) {
            printTree(node.right, sb, childPrefix + "├── R: ", childPrefix + "│   ");
            printTree(node.left,  sb, childPrefix + "└── L: ", childPrefix + "    ");
        }
    }


    // Quick smoke test
    public static void main(String[] args) {
        TreapMap<Integer, String> treap = new TreapMap<>(42L);

        int[] keys = {5, 3, 8, 1, 4, 7, 10, 2, 6, 9};
        System.out.println("Inserting: ");
        for (int k : keys) {
            treap.put(k, "v" + k);
            System.out.print(k + " ");
        }
        System.out.println();

        System.out.println("In-order: " + treap);
        System.out.println("Size:     " + treap.size());
        System.out.println("Height:   " + treap.height());
        System.out.println("Valid:    " + treap.isValid());
        System.out.println("Tree:\n" + treap.toBinaryTreeString());

        System.out.println("get(5)   = " + treap.get(5));
        System.out.println("floor(6) = " + treap.floorEntry(6));
        System.out.println("ceil(6)  = " + treap.ceilingEntry(6));
        System.out.println("lower(6) = " + treap.lowerEntry(6));
        System.out.println("higher(6)= " + treap.higherEntry(6));
        System.out.println("subMap(3,7) = " + treap.subMap(3, 7));

        System.out.println("\nRemoving 5, 1, 10 ...");
        treap.remove(5); treap.remove(1); treap.remove(10);
        System.out.println("In-order: " + treap);
        System.out.println("Valid:    " + treap.isValid());
    }
}