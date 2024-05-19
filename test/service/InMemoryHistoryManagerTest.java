package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanagement.Task;
import taskmanagement.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;
    private Task task;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
        task = new Task("taskTest", "testTasksEqualityById", TaskStatus.NEW, 1);
    }

    @Test
    void add() {
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history);
        assertEquals(1, history.size());
    }

    @Test
    void addTwoIdenticalTasks() {
        historyManager.add(task);
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history);
        assertEquals(1, history.size());
    }

    @Test
    void getHistory() {
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertEquals(history, historyManager.getHistory());
    }

    @Test
    void deleteTaskFromTheHistory() {
        Task task2 = new Task("taskTest", "testTasksEqualityById", TaskStatus.NEW, 2);
        historyManager.add(task);
        historyManager.add(task2);
        final List<Task> history1 = historyManager.getHistory();
        assertEquals(2, history1.size());
        historyManager.remove(task2.getId());
        final List<Task> history2 = historyManager.getHistory();
        assertEquals(1, history2.size());
    }
}