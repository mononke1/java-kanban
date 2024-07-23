package service;

import java.util.ArrayList;

public class HandMadeLinkedList<T> {
    private Node<T> head;
    private Node<T> tail;

    public void linkLast(T element) {
        final Node<T> oldTail = tail;
        final Node<T> newNode = new Node<>(oldTail, element, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.setNext(newNode);
        }
    }

    public void removeNode(Node<T> node) {
        if (node == null) {
            return;
        }
        final Node<T> prev = node.getPrev();
        final Node<T> next = node.getNext();

        if (prev != null) {
            prev.setNext(next);
        } else {
            head = next;
        }

        if (next != null) {
            next.setPrev(prev);
        } else {
            tail = prev;
        }

        node.setData(null);
    }

    public ArrayList<T> getTasks() {
        ArrayList<T> tasks = new ArrayList<>();
        Node<T> current = head;
        while (current != null) {
            tasks.add(current.getData());
            current = current.getNext();
        }
        return tasks;
    }

    public Node<T> getTail() {
        return tail;
    }
}
