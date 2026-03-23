package project20280.list;

import project20280.interfaces.List;

import java.util.Iterator;

public class SinglyLinkedList<E> implements List<E> {

    private static class Node<E> {

        private final E element;            // reference to the element stored at this node

        /**
         * A reference to the subsequent node in the list
         */
        private Node<E> next;         // reference to the subsequent node in the list

        /**
         * Creates a node with the given element and next node.
         *
         * @param e the element to be stored
         * @param n reference to a node that should follow the new node
         */
        public Node(E e, Node<E> n) {
            element = e;
            next = n;
        }

        // Accessor methods

        /**
         * Returns the element stored at the node.
         *
         * @return the element stored at the node
         */
        public E getElement() {
            return element;
        }

        /**
         * Returns the node that follows this one (or null if no such node).
         *
         * @return the following node
         */
        public Node<E> getNext() {
            return next;
        }

        // Modifier methods

        /**
         * Sets the node's next reference to point to Node n.
         *
         * @param n the node that should follow this one
         */
        public void setNext(Node<E> n) {
            next=n;
        }
    } //----------- end of nested Node class -----------

    /**
     * The head node of the list
     */
    private Node<E> head = null;               // head node of the list (or null if empty)


    /**
     * Number of nodes in the list
     */
    private int size = 0;                      // number of nodes in the list

    public SinglyLinkedList() {
    }              // constructs an initially empty list

    //@Override
    public int size() {
        if(head == null) return 0;
        return size;
    }

    //@Override
    public boolean isEmpty() {
        return head == null;
    }

    @Override
    public E get(int position) {

        if (head == null) return null;
        int index = 0;
        Node<E> n = head;

        while(index < position && n != null)
        {
            n = n.next;
            index ++;
        }
        return n.element;
    }

    @Override
    public void add(int position, E e) {
        if (position < 0 || position > size) {
            System.out.println("Position " + position + " is out of bounds.");
            return;
        }

        if (position == 0) {
            addFirst(e);
            return;
        }

        Node<E> newest = new Node<>(e, null);
        Node<E> current = head;

        for (int i = 0; i < position - 1; i++) {
            current = current.next;
        }

        newest.next = current.next;
        current.next = newest;
        size++;

    }


    @Override
    public void addFirst(E e) {
        Node<E> newNode = new Node<>(e,null);

        // Update the next reference of the new node to point to the current head
        newNode.next = head;
        head = newNode;
        size++;

    }

    @Override
    public void addLast(E e) {
        Node<E> newest = new Node<>(e, null);

        if (head == null) {
            head = newest;
        } else {
            Node<E> n = head;
            while (n.next != null) n = n.next;

            n.next = newest;
        }

        size++;
    }

    @Override
    public E remove(int position) {
        // Check if the list is empty
        if (head == null) {
            System.out.println("List is empty. Unable to remove element at position " + position + ".");
            return null;
        }

        // Check if the position is valid
        if (position < 0) {
            System.out.println("Invalid position. Please provide a non-negative position.");
            return null;
        }

        E removedData;

        // If removing the first element, update the head and return the removed data
        if (position == 0) {
            removedData = head.element;
            head = head.next;
            return removedData;
        }

        int index = 0;
        Node<E> current = head;

        // Traverse to the node just before the specified position
        while (index < position - 1 && current != null) {
            current = current.next;
            index++;
        }

        // Check if the position is out of bounds
        if (current == null || current.next == null) {
            System.out.println("Position " + position + " is out of bounds.");
            return null;
        }

        // Store the removed data and remove the node at the specified position
        removedData = current.next.element;
        current.next = current.next.next;

        // Return the removed data
        size--;
        return removedData;
    }

    @Override
    public E removeFirst() {
        // Check if the list is empty
        if (head == null) {
            System.out.println("List is empty. Nothing to remove.");
            return null;
        }

        // Store the removed data and update the head
        E removedData = head.element;
        head = head.next;

        // Return the removed data
        size--;
        return removedData;

    }

    @Override
    public E removeLast() {
        // Check if the list is empty
        if (head == null) {
            System.out.println("List is empty. Nothing to remove.");
            return null;
        }

        // If there is only one element in the list, remove it and update the head
        if (head.next == null) {
            E removedData = head.element;
            head = null;
            return removedData;
        }

        Node<E> current = head;
        while (current.next.next != null) current = current.next;

        E removedData = current.next.element;
        current.next = null;
        size--;
        return removedData;
    }




    //@Override
    public Iterator<E> iterator() {
        return new SinglyLinkedListIterator<E>();
    }

    private class SinglyLinkedListIterator<E> implements Iterator<E> {
        Node<E> curr = (Node<E>) head;

        @Override
        public boolean hasNext() {
            return curr != null;
        }

        @Override
        public E next() {
            E res = curr.getElement();
            curr = curr.next;
            return res;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        Node<E> curr = head;
        while (curr != null) {
            sb.append(curr.getElement());
            if (curr.getNext() != null)
                sb.append(", ");
            curr = curr.getNext();
        }
        sb.append("]");
        return sb.toString();
    }

    public static void main(String[] args) {
        SinglyLinkedList<Integer> ll = new SinglyLinkedList<Integer>();
        System.out.println("ll " + ll + " isEmpty: " + ll.isEmpty());
        //LinkedList<Integer> ll = new LinkedList<Integer>();

        ll.addFirst(0);
        ll.addFirst(1);
        ll.addFirst(2);
        ll.addFirst(3);
        ll.addFirst(4);
        ll.addLast(-1);
        //ll.removeLast();
        //ll.removeFirst();
        //System.out.println("I accept your apology");
        //ll.add(3, 2);
        System.out.println(ll);
        ll.remove(5);
        System.out.println(ll);

    }

    public static <E> void reverse(SinglyLinkedList<E> list) {
        SinglyLinkedList.Node<E> prev = null;
        SinglyLinkedList.Node<E> curr = list.head;
        SinglyLinkedList.Node<E> next;

        while (curr != null) {
            next = curr.next;
            curr.next = prev;
            prev = curr;
            curr = next;
        }

        list.head = prev;
    }

    public static <E> SinglyLinkedList<E> cloneList(SinglyLinkedList<E> list) {
        SinglyLinkedList<E> clone = new SinglyLinkedList<>();
        SinglyLinkedList.Node<E> curr = list.head;

        while (curr != null) {
            clone.addLast(curr.getElement());
            curr = curr.next;
        }

        return clone;
    }

    public static SinglyLinkedList<Integer> sortedMerge(
            SinglyLinkedList<Integer> l1,
            SinglyLinkedList<Integer> l2) {

        SinglyLinkedList<Integer> result = new SinglyLinkedList<>();
        SinglyLinkedList.Node<Integer> p1 = l1.head;
        SinglyLinkedList.Node<Integer> p2 = l2.head;

        while (p1 != null && p2 != null) {
            if (p1.getElement() <= p2.getElement()) {
                result.addLast(p1.getElement());
                p1 = p1.next;
            } else {
                result.addLast(p2.getElement());
                p2 = p2.next;
            }
        }

        while (p1 != null) {
            result.addLast(p1.getElement());
            p1 = p1.next;
        }

        while (p2 != null) {
            result.addLast(p2.getElement());
            p2 = p2.next;
        }

        return result;
    }

}



