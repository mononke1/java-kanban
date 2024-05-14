package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanagement.Task;
import taskmanagement.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryHistoryManagerTest {

    @BeforeEach
    void asd() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        Task task = new Task("taskTest", "testTasksEqualityById", TaskStatus.NEW, 1);
        historyManager.add(task);
    }

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
    void getHistory() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        Task task = new Task("taskTest", "testTasksEqualityById", TaskStatus.NEW, 1);
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertEquals(history, historyManager.getHistory());
    }
}