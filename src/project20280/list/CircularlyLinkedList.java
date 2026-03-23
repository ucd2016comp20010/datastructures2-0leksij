package project20280.list;

import project20280.interfaces.List;

import java.util.Iterator;

public class CircularlyLinkedList<E> implements List<E> {

    private class Node<T> {
        private final T data;
        private Node<T> next;

        public Node(T e, Node<T> n) {
            data = e;
            next = n;
        }

        public T getData() {
            return data;
        }

        public void setNext(Node<T> n) {
            next = n;
        }

        public Node<T> getNext() {
            return next;
        }
    }

    private Node<E> tail = null;
    private int size = 0;

    public CircularlyLinkedList() {

    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public E get(int i) {
        if (i < 0 || i >= size) throw new IndexOutOfBoundsException("Index: " + i + ", Size: " + size);

        Node<E> current = tail.getNext();
        for (int index = 0; index < i; index++) current = current.getNext();

        return current.getData();
    }

    /**
     * Inserts the given element at the specified index of the list, shifting all
     * subsequent elements in the list one position further to make room.
     *
     * @param i the index at which the new element should be stored
     * @param e the new element to be stored
     */
    @Override
    public void add(int i, E e) {
        if (i < 0 || i > size) throw new IndexOutOfBoundsException("Index: " + i + ", Size: " + size);

        if (isEmpty()) {
            tail = new Node<>(e, null);
            tail.setNext(tail); // Points to itself
        }
        else if (i == 0) {
            Node<E> newNode = new Node<>(e, tail.getNext());
            tail.setNext(newNode);
        }
        else {
            Node<E> current = tail.getNext();
            for (int index = 0; index < i - 1; index++) current = current.getNext();

            Node<E> newNode = new Node<>(e, current.getNext());
            current.setNext(newNode);
            if (i == size) tail = newNode; // Update tail if adding at the end

        }
        size++;
    }

    @Override
    public E remove(int i) {
        if (isEmpty() || i < 0 || i >= size) throw new IndexOutOfBoundsException("Index: " + i + ", Size: " + size);

        E removedData;
        if (size == 1) {
            removedData = tail.getData();
            tail = null;
        }
        else if (i == 0) {
            removedData = tail.getNext().getData();
            tail.setNext(tail.getNext().getNext());
        }
        else {
            Node<E> current = tail.getNext();
            for (int index = 0; index < i - 1; index++) current = current.getNext();

            removedData = current.getNext().getData();
            current.setNext(current.getNext().getNext());
            if (i == size - 1) tail = current; // Update tail if removing the last element

        }
        size--;
        return removedData;
    }

    public void rotate() {
        if (!isEmpty()) tail = tail.getNext();
    }


    private class CircularlyLinkedListIterator<E> implements Iterator<E> {
        Node<E> curr = (Node<E>) tail;

        @Override
        public boolean hasNext() {
            return curr != tail;
        }

        @Override
        public E next() {
            E res = curr.data;
            curr = curr.next;
            return res;
        }

    }

    @Override
    public Iterator<E> iterator() {
        return new CircularlyLinkedListIterator<E>();
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public E removeFirst() {
        if (isEmpty()) return null;

        Node<E> head = tail.next;
        if (head == tail) { // only one element
            tail = null;
        } else {
            tail.next = head.next;
        }
        size--;
        return head.data;
    }

    @Override
    public E removeLast() {
        if (isEmpty()) return null;

        Node<E> current = tail.next;
        if (current == tail) { // only one element
            tail = null;
            size--;
            return current.data;
        }

        while (current.next != tail) {
            current = current.next;
        }

        Node<E> oldTail = tail;
        current.next = tail.next; // bypass old tail
        tail = current;
        size--;
        return oldTail.data;
    }


    @Override
    public void addFirst(E e) {
        Node<E> newest = new Node<>(e, null);
        if (isEmpty()) {
            newest.next = newest; // points to itself
            tail = newest;
        } else {
            newest.next = tail.next; // new node points to head
            tail.next = newest;      // tail points to new node
        }
        size++;
    }

    @Override
    public void addLast(E e) {
        addFirst(e);     // add to front
        tail = tail.next; // then move tail to the new node
    }



    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        Node<E> curr = tail;
        do {
            curr = curr.next;
            sb.append(curr.data);
            if (curr != tail) {
                sb.append(", ");
            }
        } while (curr != tail);
        sb.append("]");
        return sb.toString();
    }


    public static void main(String[] args) {
        CircularlyLinkedList<Integer> ll = new CircularlyLinkedList<Integer>();
        for (int i = 10; i < 20; ++i) {
            ll.addLast(i);
        }

        System.out.println(ll);

        ll.removeFirst();
        System.out.println(ll);

        ll.removeLast();
        System.out.println(ll);

        ll.rotate();
        System.out.println(ll);

        ll.removeFirst();
        ll.rotate();
        System.out.println(ll);

        ll.removeLast();
        ll.rotate();
        System.out.println(ll);

        for (Integer e : ll) {
            System.out.println("value: " + e);
        }

    }
}
