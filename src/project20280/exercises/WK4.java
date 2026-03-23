package project20280.exercises;

import project20280.interfaces.Position;
import project20280.tree.LinkedBinaryTree;

public class WK4 {


    //Q1(h) – Height + Count Recursive Calls


        private static int heightCalls = 0;

        public static <E> int heightWithCount(LinkedBinaryTree<E> tree) {
            heightCalls = 0;
            int h = heightRecursive(tree, tree.root());
            System.out.println("Height: " + h);
            System.out.println("Recursive calls: " + heightCalls);
            return h;
        }

        private static <E> int heightRecursive(LinkedBinaryTree<E> tree, Position<E> p) {
            heightCalls++;

            if (tree.isExternal(p))
                return 0;

            int h = 0;
            for (Position<E> child : tree.children(p)) {
                h = Math.max(h, heightRecursive(tree, child));
            }
            return h + 1;
        }

    //Q1 (i)Diameter of Binary Tree (O(n))


        private static int diameter = 0;

        public static <E> int diameter(LinkedBinaryTree<E> tree) {
            diameter = 0;
            diameterHelper(tree, tree.root());
            return diameter;
        }

        private static <E> int diameterHelper(LinkedBinaryTree<E> tree, Position<E> p) {
            if (p == null)
                return -1;

            int leftHeight = -1;
            int rightHeight = -1;

            if (tree.left(p) != null)
                leftHeight = diameterHelper(tree, tree.left(p));

            if (tree.right(p) != null)
                rightHeight = diameterHelper(tree, tree.right(p));

            diameter = Math.max(diameter, leftHeight + rightHeight + 2);

            return Math.max(leftHeight, rightHeight) + 1;
        }

    //Q2 – Count External Nodes (Leaves)


        public static <E> int countExternal(LinkedBinaryTree<E> tree, Position<E> p) {
            if (tree.isExternal(p))
                return 1;

            int count = 0;
            for (Position<E> child : tree.children(p))
                count += countExternal(tree, child);

            return count;
        }

    //Q3 – Count Left External Nodes

        public static <E> int countLeftLeaves(LinkedBinaryTree<E> tree, Position<E> p) {
            int count = 0;

            if (tree.left(p) != null) {
                if (tree.isExternal(tree.left(p)))
                    count++;
                else
                    count += countLeftLeaves(tree, tree.left(p));
            }

            if (tree.right(p) != null)
                count += countLeftLeaves(tree, tree.right(p));

            return count;
        }
    //Q4
        /*
Preorder = Root  Left Right
        E
       / \
      X   U
     / \   \
    A   M   N
         \
          F

Inorder = Left Root  Right
        M
       / \
      X   U
     / \   \
    E   A   N
         \
          F

Postorder = Left  Right  Root
        N
       / \
      M   U
     / \
    E   F
     \
      X
       \
        A

          * */
    //Q5 – Count Total Descendants


        public static <E> int countDescendants(LinkedBinaryTree<E> tree, Position<E> p) {
            int count = 0;

            for (Position<E> child : tree.children(p)) {
                count += 1;
                count += countDescendants(tree, child);
            }

            return count;
        }

    //Q6 – Count Total Descendants
    }
