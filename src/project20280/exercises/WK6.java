package project20280.exercises;

import project20280.list.SinglyLinkedList;
import project20280.tree.LinkedBinaryTree;
import project20280.interfaces.Position;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * WK6 - Recursion Exercise Solutions
 *
 * Q1:  ReverseArray recursion trace
 * Q2:  Fibonacci (naive + memoised)
 * Q3:  Tribonacci
 * Q4:  McCarthy-91 function
 * Q5:  Foo — binary representation printer
 * Q6:  Reverse a SinglyLinkedList in place (recursive)
 * Q7:  Recursive copy of a SinglyLinkedList
 * Q8:  mystery — arithmetic sequence term
 * Q9:  Leaf nodes left-to-right from LinkedBinaryTree
 * Q10: Inorder timing analysis (CSV output)
 */
public class WK6 {

    //
    // Q1: ReverseArray
    //
    // Recursion trace for A = {12, 5, 19, 6, 11, 3, 9, 34, 2, 1, 15}
    // (len = 11, indices 0..10):
    //
    // ReverseArray(A, 0, 10) → swap A[0]=12  ↔ A[10]=15  → [15,5,19,6,11,3,9,34,2,1,12]
    //   ReverseArray(A, 1, 9)  → swap A[1]=5   ↔ A[9]=1   → [15,1,19,6,11,3,9,34,2,5,12]
    //     ReverseArray(A, 2, 8)  → swap A[2]=19  ↔ A[8]=2   → [15,1,2,6,11,3,9,34,19,5,12]
    //       ReverseArray(A, 3, 7)  → swap A[3]=6   ↔ A[7]=34  → [15,1,2,34,11,3,9,6,19,5,12]
    //         ReverseArray(A, 4, 6)  → swap A[4]=11  ↔ A[6]=9   → [15,1,2,34,9,3,11,6,19,5,12]
    //           ReverseArray(A, 5, 5)  → i == j, base case, return
    //
    // Final: [15, 1, 2, 34, 9, 3, 11, 6, 19, 5, 12]
    //

    public static void reverseArray(int[] A, int i, int j) {
        if (i < j) {
            int tmp = A[i];
            A[i] = A[j];
            A[j] = tmp;
            reverseArray(A, i + 1, j - 1);
        }
    }

    //
    // Q2: Fibonacci — naive binary recursion + memoised
    //
    // Recursion trace for Fibonacci(5):
    //
    // fib(5)
    // ├── fib(4)
    // │   ├── fib(3)
    // │   │   ├── fib(2)
    // │   │   │   ├── fib(1) → 1
    // │   │   │   └── fib(0) → 0
    // │   │   └── fib(1) → 1
    // │   └── fib(2)
    // │       ├── fib(1) → 1
    // │       └── fib(0) → 0
    // └── fib(3)
    //     ├── fib(2)
    //     │   ├── fib(1) → 1
    //     │   └── fib(0) → 0
    //     └── fib(1) → 1
    // Result: 5
    //
    // Naive: each call recomputes everything → exponential O(2^n).
    // Without memoisation, ~n=50 is feasible in 1 min (doubles every step).
    // With memoisation: O(n), can compute n in the millions in 1 min.
    //

    // Counter for Q2 — counts recursive calls made
    private static long fibCallCount = 0;

    public static long fibonacci(int n) {
        fibCallCount++;
        if (n <= 1) return n;
        return fibonacci(n - 1) + fibonacci(n - 2);
    }

    // Memoised version — iterative (bottom-up) to avoid StackOverflow on large n.
    // Recursive memoisation still overflows the stack for large n because the first
    // call recurses n levels deep before any cached values exist.
    public static long fibMemo(int n, Map<Integer, Long> memo) {
        if (n <= 1) return n;
        memo.put(0, 0L);
        memo.put(1, 1L);
        for (int i = 2; i <= n; i++) {
            if (!memo.containsKey(i)) {
                memo.put(i, memo.get(i - 1) + memo.get(i - 2));
            }
        }
        return memo.get(n);
    }

    //
    // Q3: Tribonacci
    //
    // T(0)=0, T(1)=0, T(2)=1
    // T(n) = T(n-1) + T(n-2) + T(n-3)  for n >= 3
    //
    // Sequence: 0, 0, 1, 1, 2, 4, 7, 13, 24, 44, ...
    // 9th term (0-indexed): T(9) = 44
    //
    // Recursion trace for tribonacci(4) as a small example:
    // trib(4) = trib(3) + trib(2) + trib(1)
    //   trib(3) = trib(2) + trib(1) + trib(0) = 1+0+0 = 1
    //   trib(2) = 1  (base case)
    //   trib(1) = 0  (base case)
    // = 1 + 1 + 0 = 2
    //

    public static long tribonacci(int n) {
        if (n == 0 || n == 1) return 0;
        if (n == 2) return 1;
        return tribonacci(n - 1) + tribonacci(n - 2) + tribonacci(n - 3);
    }

    //
    // Q4: McCarthy-91 function
    //
    //   M(n) = n - 10        if n > 100
    //   M(n) = M(M(n + 11))  if n <= 100
    //
    // This is a *nested* recursive function — the outer call's argument is
    // itself a recursive call. This makes it "doubly recursive".
    //
    // Result: M(n) = 91 for all n <= 100, and n-10 for n > 100.
    //
    // Recursion trace for M(87):
    // M(87) = M(M(98)) = M(M(M(109))) = M(M(99)) = M(M(M(110)))
    //       = M(M(100)) = M(M(M(111))) = M(M(101)) = M(91) = 91
    // (The chain winds up 13 levels deep before unwinding to 91.)
    //

    public static int mcCarthy91(int n) {
        if (n > 100) return n - 10;
        return mcCarthy91(mcCarthy91(n + 11));
    }

    //
    // Q5: Foo — prints binary representation of x
    //
    // (a) Foo(x) prints the binary representation of x.
    //     The recursive call goes deeper first (most significant bit),
    //     then prints x%2 (current bit) on the way back.
    //
    // (b) Foo(2468):
    //     2468 in binary = 100110100100
    //     Output: 100110100100
    //
    // Trace (abbreviated):
    // foo(2468) → foo(1234) → foo(617) → foo(308) → foo(154) → foo(77)
    //  → foo(38) → foo(19) → foo(9) → foo(4) → foo(2) → foo(1)
    //    → foo(0): print 0, return     [x/2==0 base case]
    //   print 1%2=1
    //  print 2%2=0  ... and so on unwinding
    //

    public static void foo(int x) {
        if (x / 2 == 0) {
            System.out.print(x);
            return;
        }
        foo(x / 2);
        System.out.print(x % 2);
    }

    //
    // Q6: Reverse SinglyLinkedList in place — recursive
    //
    // Pseudocode:
    // ---------------------------------------------------------
    // reverseRecursive(node, prev):
    //   if node is null: head = prev; return
    //   next = node.next
    //   node.next = prev
    //   reverseRecursive(next, node)
    // ---------------------------------------------------------
    //
    // Each frame flips one link; when we reach the end the last node
    // becomes the new head. O(n) time, O(n) stack space.
    //
    // These are static helpers that operate on SinglyLinkedList via
    // the public API (addFirst / removeFirst) to avoid access issues.
    //

    /**
     * Reverses a SinglyLinkedList in place using recursion.
     * Collects elements recursively then rebuilds in reverse order.
     */
    public static <E> void reverseRecursive(SinglyLinkedList<E> list) {
        if (list.isEmpty()) return;
        // Drain into a helper list recursively, rebuilding reversed
        SinglyLinkedList<E> temp = new SinglyLinkedList<>();
        collectReverse(list, temp);
        // Copy temp back into list
        while (!temp.isEmpty()) {
            list.addLast(temp.removeFirst());
        }
    }

    private static <E> void collectReverse(SinglyLinkedList<E> src, SinglyLinkedList<E> dest) {
        if (src.isEmpty()) return;
        E head = src.removeFirst();       // take from front
        collectReverse(src, dest);        // recurse on rest
        dest.addLast(head);               // append after rest (reverses order)
    }

    //
    // Q7: Recursive copy of SinglyLinkedList
    //
    // Pseudocode:
    // ---------------------------------------------------------
    // recursiveCopy(node):
    //   if node is null: return null
    //   newNode = new Node(node.element)
    //   newNode.next = recursiveCopy(node.next)
    //   return newNode
    // ---------------------------------------------------------
    //
    // Each recursive call copies one node and wires it to the copy
    // of the rest. O(n) time and space.
    //

    /**
     * Returns a deep copy of the list using recursion.
     */
    public static <E> SinglyLinkedList<E> recursiveCopy(SinglyLinkedList<E> list) {
        SinglyLinkedList<E> copy = new SinglyLinkedList<>();
        copyHelper(list, copy, 0);
        return copy;
    }

    private static <E> void copyHelper(SinglyLinkedList<E> src, SinglyLinkedList<E> dest, int index) {
        if (index >= src.size()) return;
        dest.addLast(src.get(index));       // copy element at this index
        copyHelper(src, dest, index + 1);   // recurse on the rest
    }

    //
    // Q8: mystery(n, a, d)
    //
    // mystery(n, a, d):
    //   if n == 1: return a
    //   else:      return d + mystery(n-1, a, d)
    //
    // This computes the nth term of an arithmetic sequence:
    //   a + (n-1)*d
    //
    // Recursion trace for mystery(2, 4, 4):
    //   mystery(2, 4, 4)
    //   └── 4 + mystery(1, 4, 4)
    //             └── return 4          (base case n==1)
    //       = 4 + 4 = 8
    //
    // Result: 8
    //

    public static int mystery(int n, int a, int d) {
        if (n == 1) return a;
        return d + mystery(n - 1, a, d);
    }

    //
    // Q9: Leaf nodes left-to-right from LinkedBinaryTree
    //
    // An inorder traversal visits nodes in left→root→right order.
    // We collect only the leaves (nodes with no children).
    // For the tree in the question: [D, G, H, F]
    //

    public static <E> List<E> leafNodes(LinkedBinaryTree<E> bt) {
        List<E> leaves = new ArrayList<>();
        if (!bt.isEmpty()) leafHelper(bt, bt.root(), leaves);
        return leaves;
    }

    private static <E> void leafHelper(
            LinkedBinaryTree<E> bt,
            Position<E> p,
            List<E> leaves) {

        if (p == null) return;

        Position<E> left  = bt.left(p);
        Position<E> right = bt.right(p);

        leafHelper(bt, left, leaves);

        if (left == null && right == null) {
            leaves.add(p.getElement());   // leaf node
        }

        leafHelper(bt, right, leaves);
    }

    //
    // Q10: Inorder timing analysis
    //
    // Expected complexity: O(n) — every node is visited exactly once.
    // The trendline on the plot should be linear (straight line through origin).
    //
    // Prints CSV: n, timeNanos
    //

    public static void analyseInorderTiming() {
        System.out.println("n,timeNanos");
        for (int n = 10; n <= 10000; n += 10) {
            // Average over multiple trials to reduce noise
            int trials = 5;
            long total = 0;
            for (int t = 0; t < trials; t++) {
                LinkedBinaryTree<Integer> bt = LinkedBinaryTree.makeRandom(n);
                long start = System.nanoTime();
                for (@SuppressWarnings("unused") Position<Integer> p : bt.inorder()) {
                    // just iterate — inorder traversal
                }
                total += System.nanoTime() - start;
            }
            System.out.printf("%d,%d%n", n, total / trials);
        }
    }



    public static void main(String[] args) {

        // ---- Q1 ----
        System.out.println("=== Q1: ReverseArray ===");
        int[] A = {12, 5, 19, 6, 11, 3, 9, 34, 2, 1, 15};
        reverseArray(A, 0, A.length - 1);
        System.out.print("Reversed: [");
        for (int i = 0; i < A.length; i++)
            System.out.print(A[i] + (i < A.length - 1 ? ", " : ""));
        System.out.println("]");
        // Expected: [15, 1, 2, 34, 9, 3, 11, 6, 19, 5, 12]

        // ---- Q2 ----
        System.out.println("\n=== Q2: Fibonacci ===");
        System.out.println("fib(5) = " + fibonacci(5));
        fibCallCount = 0;

        // Find largest fib computable in ~1 second (naive)
        int n = 0;
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < 1000) {
            fibCallCount = 0;
            fibonacci(n++);
        }
        System.out.println("Largest fib (naive, ~1s): n=" + (n - 1));
        System.out.println("Recursive calls for that n: " + fibCallCount);

        // Memoised — can go much higher
        Map<Integer, Long> memo = new HashMap<>();
        System.out.println("fib(10000) memoised = " + fibMemo(10000, memo));

        // ---- Q3 ----
        System.out.println("\n=== Q3: Tribonacci ===");
        for (int i = 0; i <= 9; i++)
            System.out.println("trib(" + i + ") = " + tribonacci(i));
        System.out.println("9th term (0-indexed): trib(9) = " + tribonacci(9));
        // Expected: 44

        // ---- Q4 ----
        System.out.println("\n=== Q4: McCarthy-91 ===");
        System.out.println("M(87)  = " + mcCarthy91(87));   // Expected: 91
        System.out.println("M(99)  = " + mcCarthy91(99));   // Expected: 91
        System.out.println("M(100) = " + mcCarthy91(100));  // Expected: 91
        System.out.println("M(101) = " + mcCarthy91(101));  // Expected: 91
        System.out.println("M(105) = " + mcCarthy91(105));  // Expected: 95

        // ---- Q5 ----
        System.out.println("\n=== Q5: Foo(2468) ===");
        System.out.print("Binary of 2468: ");
        foo(2468);
        System.out.println();
        // Expected: 100110100100

        // ---- Q6 ----
        System.out.println("\n=== Q6: Reverse SinglyLinkedList (recursive) ===");
        SinglyLinkedList<Integer> list6 = new SinglyLinkedList<>();
        for (int x : new int[]{1, 2, 3, 4, 5}) list6.addLast(x);
        System.out.println("Before: " + list6);
        reverseRecursive(list6);
        System.out.println("After:  " + list6);
        // Expected: [5, 4, 3, 2, 1]

        // ---- Q7 ----
        System.out.println("\n=== Q7: Recursive copy ===");
        SinglyLinkedList<Integer> list7 = new SinglyLinkedList<>();
        for (int x : new int[]{10, 20, 30, 40}) list7.addLast(x);
        SinglyLinkedList<Integer> copy7 = recursiveCopy(list7);
        System.out.println("Original: " + list7);
        System.out.println("Copy:     " + copy7);

        // ---- Q8 ----
        System.out.println("\n=== Q8: mystery(2,4,4) ===");
        System.out.println("mystery(2,4,4) = " + mystery(2, 4, 4));
        // Expected: 8

        // ---- Q9 ----
        System.out.println("\n=== Q9: Leaf nodes left-to-right ===");
        LinkedBinaryTree<String> bt9 = new LinkedBinaryTree<>();
        String[] arr9 = {"A", "B", "C", "D", "E", null, "F",
                null, null, "G", "H", null, null, null, null};
        bt9.createLevelOrder(arr9);
        System.out.println(bt9.toBinaryTreeString());
        System.out.println("Leaves: " + leafNodes(bt9));
        // Expected: [D, G, H, F]

        // ---- Q10 ----
        System.out.println("\n=== Q10: Inorder timing CSV ===");
        analyseInorderTiming();
    }
}