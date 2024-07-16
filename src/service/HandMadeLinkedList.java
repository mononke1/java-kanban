package service;

import java.util.ArrayList;

public class HandMadeLinkedList<Task> {
    private Node<Task> head;
    private Node<Task> tail;

    public void linkLast(Task element) {
        final Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node<>(oldTail, element, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.setNext(newNode);
        }
    }

    public void removeNode(Node<Task> node) {
        if (node == null) {
            return;
        }
        final Node<Task> prev = node.getPrev();
        final Node<Task> next = node.getNext();

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

    public ArrayList<Task> getTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        Node<Task> current = head;
        while (current != null) {
            tasks.add(current.getData());
            current = current.getNext();
        }
        return tasks;
    }

    public Node<Task> getTail() {
        return tail;
    }
}