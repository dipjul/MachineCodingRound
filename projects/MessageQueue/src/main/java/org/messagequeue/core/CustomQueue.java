package org.messagequeue.core;

import java.util.LinkedList;

public class CustomQueue<T> {
    private final LinkedList<T> queue = new LinkedList<>();

    public void enqueue(T item) {
        queue.addLast(item);
    }

    public T dequeue() {
        return queue.pollFirst();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}