package util;

import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;

public class FixedSizeHeap<T> implements Iterable<T> {
    private int maxSize;

    private PriorityQueue<T> heap;

    public FixedSizeHeap(int maxSize) {
        this(maxSize, null);
    }

    public FixedSizeHeap(int maxSize, Comparator<T> comparator) {
        this.maxSize = Math.max(maxSize, 0);
        this.heap = new PriorityQueue<>(maxSize + 2, comparator);
    }

    public synchronized void insert(T elem) {
        heap.offer(elem);
        if (heap.size() > maxSize) {
            heap.poll();
        }
    }

    public T pop() {
        return heap.poll();
    }

    public boolean isEmpty() {
        return heap.isEmpty();
    }

    public boolean isNonEmpty() {
        return !heap.isEmpty();
    }

    @Override
    public Iterator<T> iterator() {
        return heap.iterator();
    }

    public int size() {
        return heap.size();
    }

    public void clear() {
        heap.clear();
    }

}
