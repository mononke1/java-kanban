package service;

import org.junit.jupiter.api.Test;
import taskmanagement.Task;
import taskmanagement.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryHistoryManagerTest {


    @Test
    void add() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        Task task = new Task("taskTest", "testTasksEqualityById", TaskStatus.NEW, 1);
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history);
        assertEquals(1, history.size());
    }

    @Test
    void addTwoIdenticalTasks() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        Task task = new Task("taskTest", "testTasksEqualityById", TaskStatus.NEW, 1);
        historyManager.add(task);
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history);
        assertEquals(1, history.size());
    }

    @Test
    void getHistory() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        Task task = new Task("taskTest", "testTasksEqualityById", TaskStatus.NEW, 1);
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertEquals(history, historyManager.getHistory());
    }

    @Test
    void deleteTaskFromTheHistory() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        Task task1 = new Task("taskTest", "testTasksEqualityById", TaskStatus.NEW, 1);
        Task task2 = new Task("taskTest", "testTasksEqualityById", TaskStatus.NEW, 2);
        historyManager.add(task1);
        historyManager.add(task2);
        final List<Task> history1 = historyManager.getHistory();
        assertEquals(2, history1.size());
        historyManager.remove(task2.getId());
        final List<Task> history2 = historyManager.getHistory();
        assertEquals(1, history2.size());
    }
}