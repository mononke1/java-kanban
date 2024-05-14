package service;

import taskmanagement.Task;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final LinkedList<Task> history;
    private static final int HISTORY_SIZE_LIMIT = 10;

    public InMemoryHistoryManager() {
        history = new LinkedList<>();
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }


    @Override
    public void add(Task task) {
        if (history.size() < HISTORY_SIZE_LIMIT) {
            history.addLast(task);
        } else {
            history.removeFirst();
            history.addLast(task);
        }
    }
}
