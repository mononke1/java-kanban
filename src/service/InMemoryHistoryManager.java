package service;

import taskmanagement.Task;

import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final HashMap<Integer, Node<Task>> history = new HashMap<>();
    private final HandMadeLinkedList<Task> historyLinkedList = new HandMadeLinkedList<>();

    @Override
    public List<Task> getHistory() {
        return historyLinkedList.getTasks();
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        remove(task.getId());
        historyLinkedList.linkLast(task);
        history.put(task.getId(), historyLinkedList.getTail());
    }

    @Override
    public void remove(int id) {
        final Node<Task> node = history.remove(id);
        if (node == null) {
            return;
        }
        historyLinkedList.removeNode(node);
    }
}
