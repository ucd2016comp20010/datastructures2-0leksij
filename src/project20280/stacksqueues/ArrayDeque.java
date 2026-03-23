package project20280.stacksqueues;

import project20280.interfaces.Deque;

@SuppressWarnings("unchecked")
public class ArrayDeque<E> implements Deque<E> {

    private static final int CAPACITY = 1000;
    private E[] data;
    private int front = 0;
    private int size = 0;

    public ArrayDeque(int capacity) {
        data = (E[]) new Object[capacity];
    }

    public ArrayDeque() {
        this(CAPACITY);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public E first() {
        return isEmpty() ? null : data[front];
    }

    @Override
    public E last() {
        if (isEmpty()) return null;
        int backIndex = (front + size - 1 + data.length) % data.length;
        return data[backIndex];
    }

    @Override
    public void addFirst(E e) {
        if (size == data.length) {
            throw new IllegalStateException("Deque is full");
        }
        front = (front - 1 + data.length) % data.length;
        data[front] = e;
        size++;
    }

    @Override
    public void addLast(E e) {
        if (size == data.length) {
            throw new IllegalStateException("Deque is full");
        }
        int avail = (front + size) % data.length;
        data[avail] = e;
        size++;
    }

    @Override
    public E removeFirst() {
        if (isEmpty()) return null;

        E answer = data[front];
        data[front] = null;              // help GC
        front = (front + 1) % data.length;
        size--;
        return answer;
    }

    @Override
    public E removeLast() {
        if (isEmpty()) return null;

        int backIndex = (front + size - 1 + data.length) % data.length;
        E answer = data[backIndex];
        data[backIndex] = null;          // help GC
        size--;
        return answer;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            sb.append(data[(front + i) % data.length]);
            if (i != size - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }

    public static void main(String[] args) {
        Deque<Integer> dq = new ArrayDeque<>();

        dq.addLast(1);
        dq.addLast(2);
        dq.addFirst(0);
        System.out.println(dq);   // [0, 1, 2]

        dq.removeFirst();
        dq.removeLast();
        System.out.println(dq);   // [1]
    }
}
