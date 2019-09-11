package util;

import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;

/**
 * An bounded heap
 */
public class FixedSizeHeap<T> implements Iterable<T> {

    private int maxSize;

    private PriorityQueue<T> heap;

    /**
     * Creates a heap with the specified max size that orders its elements according to their
     * Comparable natural ordering
     */
    public FixedSizeHeap(int maxSize) {
        this(maxSize, null);
    }

    /**
     * Creates a heap with the specified max size that orders its elements according to the
     * specified comparator.
     */
    public FixedSizeHeap(int maxSize, Comparator<T> comparator) {
        this.maxSize = Math.max(maxSize, 0);
        this.heap = new PriorityQueue<>(maxSize + 2, comparator);
    }

    /**
     * Inserts the specified element into this priority queue.
     * If heap size exceeds given max size of heap after insertion, will pop the heap top
     * element.
     */
    public void insert(T elem) {
        heap.offer(elem);
        if (heap.size() > maxSize) {
            heap.poll();
        }
    }

    /**
     * Pop the head top element. The method will return null when heap is empty.
     */
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

    /**
     * Remove all of the elements in the heap.
     * The heap will be empty after this call returns.
     */
    public void clear() {
        heap.clear();
    }

}
