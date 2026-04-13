package project20280.exercises;

import project20280.interfaces.Position;
import project20280.tree.LinkedBinaryTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class WK5 {

    @SuppressWarnings("unchecked")
    public static <E> void createLevelOrder(LinkedBinaryTree<E> bt, ArrayList<E> l) {
        Object[] arr = l.toArray();
        bt.createLevelOrder((E[]) arr);
    }

    //
    // Q3: Construct a binary tree from inorder + preorder traversals
    //
    // Algorithm:
    //   - preorder[preStart] is always the current subtree root.
    //   - Find that value in inorder; everything left is the left subtree,
    //     everything right is the right subtree.
    //   - Recurse, building the tree top-down using addRoot/addLeft/addRight.
    //

    public static <E> void construct(LinkedBinaryTree<E> bt, E[] inorder, E[] preorder) {
        Map<E, Integer> inorderIndex = new HashMap<>();
        for (int i = 0; i < inorder.length; i++) {
            inorderIndex.put(inorder[i], i);
        }
        int[] preStart = {0};
        Position<E> root = bt.addRoot(preorder[preStart[0]++]);
        buildTree(bt, inorder, preorder, 0, inorder.length - 1, preStart, inorderIndex, root);
    }

    private static <E> void buildTree(
            LinkedBinaryTree<E> bt,
            E[] inorder, E[] preorder,
            int inLeft, int inRight,
            int[] preStart,
            Map<E, Integer> inorderIndex,
            Position<E> current) {

        int mid = inorderIndex.get(current.getElement());

        if (mid > inLeft) {
            Position<E> leftPos = bt.addLeft(current, preorder[preStart[0]++]);
            buildTree(bt, inorder, preorder, inLeft, mid - 1, preStart, inorderIndex, leftPos);
        }

        if (mid < inRight) {
            Position<E> rightPos = bt.addRight(current, preorder[preStart[0]++]);
            buildTree(bt, inorder, preorder, mid + 1, inRight, preStart, inorderIndex, rightPos);
        }
    }


    // Q4: Root-to-leaf path
    // DFS with backtracking. A leaf is a node with no left and no right child.


    public static <E> List<List<E>> rootToLeafPaths(LinkedBinaryTree<E> bt) {
        List<List<E>> result = new ArrayList<>();
        if (bt.isEmpty()) return result;
        rootToLeafHelper(bt, bt.root(), new ArrayList<>(), result);
        return result;
    }

    private static <E> void rootToLeafHelper(
            LinkedBinaryTree<E> bt,
            Position<E> p,
            List<E> current,
            List<List<E>> result) {

        current.add(p.getElement());

        Position<E> left  = bt.left(p);
        Position<E> right = bt.right(p);

        if (left == null && right == null) {
            result.add(new ArrayList<>(current));   // leaf — snapshot the path
        } else {
            if (left  != null) rootToLeafHelper(bt, left,  current, result);
            if (right != null) rootToLeafHelper(bt, right, current, result);
        }

        current.remove(current.size() - 1);  // backtrack
    }


    // Q5: Diameter of a binary tree
    //
    // Pseudocode:
    // ---------------------------------------------------------
    // diameter(root):
    //   maxDiameter = 0
    //
    //   height(node):
    //     if node is null: return -1
    //     leftH  = height(node.left)
    //     rightH = height(node.right)
    //     maxDiameter = max(maxDiameter, leftH + rightH + 2)
    //     return max(leftH, rightH) + 1
    //
    //   height(root)
    //   return maxDiameter
    // ---------------------------------------------------------
    //
    // At each node the longest path through it has
    //   (leftH + 1) + (rightH + 1) = leftH + rightH + 2  nodes.
    // Single O(n) post-order pass tracks the global maximum.


    public static <E> int diameter(LinkedBinaryTree<E> bt) {
        if (bt.isEmpty()) return 0;
        int[] maxDiameter = {0};
        diameterHelper(bt, bt.root(), maxDiameter);
        return maxDiameter[0];
    }

    private static <E> int diameterHelper(
            LinkedBinaryTree<E> bt,
            Position<E> p,
            int[] maxDiameter) {

        if (p == null) return -1;

        Position<E> left  = bt.left(p);
        Position<E> right = bt.right(p);

        int leftH  = (left  != null) ? diameterHelper(bt, left,  maxDiameter) : -1;
        int rightH = (right != null) ? diameterHelper(bt, right, maxDiameter) : -1;

        maxDiameter[0] = Math.max(maxDiameter[0], leftH + rightH + 2);

        return Math.max(leftH, rightH) + 1;
    }

    // =========================================================================
    // Q6: Average height of random binary trees as a function of n
    //
    // For n in [50, 5000] step 50, generate 100 random trees and average
    // their heights. Prints CSV for pasting into Google Sheets.
    // Expected trendline: averageHeight ≈ c * log(n), confirming O(log n).
    // =========================================================================

    public static void analyseAverageHeight() {
        System.out.println("n,averageHeight");
        for (int n = 50; n <= 5000; n += 50) {
            double total = 0;
            int trials = 100;
            for (int t = 0; t < trials; t++) {
                LinkedBinaryTree<Integer> bt = LinkedBinaryTree.makeRandom(n);
                total += treeHeight(bt, bt.root());
            }
            System.out.printf("%d,%.4f%n", n, total / trials);
        }
    }

    public static <E> int treeHeight(LinkedBinaryTree<E> bt, Position<E> p) {
        if (p == null) return -1;
        Position<E> left  = bt.left(p);
        Position<E> right = bt.right(p);
        int leftH  = (left  != null) ? treeHeight(bt, left)  : -1;
        int rightH = (right != null) ? treeHeight(bt, right) : -1;
        return 1 + Math.max(leftH, rightH);
    }



    public static void main(String[] args) {

        // ---- Q2 ----
        System.out.println("=== Q2: Level-order construction ===");
        LinkedBinaryTree<String> btQ2 = new LinkedBinaryTree<>();
        String[] arrQ2 = {"A","B","C","D","E",null,"F",
                null,null,"G","H",null,null,null,null};
        btQ2.createLevelOrder(arrQ2);
        System.out.println(btQ2.toBinaryTreeString());

        // ---- Q3 ----
        System.out.println("=== Q3: Construct from inorder + preorder ===");
        Integer[] inorder3  = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,
                16,17,18,19,20,21,22,23,24,25,26,27,28,29,30};
        Integer[] preorder3 = {18,2,1,14,13,12,4,3,9,6,5,8,7,10,11,
                15,16,17,28,23,19,22,20,21,24,27,26,25,29,30};
        LinkedBinaryTree<Integer> btQ3 = new LinkedBinaryTree<>();
        construct(btQ3, inorder3, preorder3);
        System.out.println(btQ3.toBinaryTreeString());

        // ---- Q4 ----
        System.out.println("=== Q4: Root-to-leaf paths ===");
        Integer[] inorder4  = {0,1,2,3,4,5,6,7,8};
        Integer[] preorder4 = {5,1,0,4,2,3,7,6,8};
        LinkedBinaryTree<Integer> btQ4 = new LinkedBinaryTree<>();
        construct(btQ4, inorder4, preorder4);
        System.out.println(btQ4.toBinaryTreeString());
        System.out.println(rootToLeafPaths(btQ4));
        // Expected: [[5, 1, 0], [5, 1, 4, 2, 3], [5, 7, 6], [5, 7, 8]]

        // ---- Q5 ----
        System.out.println("=== Q5: Diameter ===");
        Integer[] inorder5  = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,
                15,16,17,18,19,20,21,22};
        Integer[] preorder5 = {6,5,3,2,1,0,4,17,10,9,8,7,16,14,13,
                12,11,15,21,20,19,18,22};
        LinkedBinaryTree<Integer> btQ5 = new LinkedBinaryTree<>();
        construct(btQ5, inorder5, preorder5);
        System.out.println(btQ5.toBinaryTreeString());
        System.out.println("Diameter: " + diameter(btQ5)); // Expected: 13

        // ---- Q6 ----
        System.out.println("=== Q6: Average height CSV ===");
        analyseAverageHeight();
    }
}