package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanagement.Epic;
import taskmanagement.Subtask;
import taskmanagement.Task;
import taskmanagement.TaskStatus;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryTaskManagerTest {

    TaskManager taskManager;
    Task task1;
    Task task2;

    @BeforeEach
    void createTaskManager() {
        taskManager = new InMemoryTaskManager();
        task1 = new Task("taskTest1", "test1", TaskStatus.NEW);
        task2 = new Task("taskTest2", "test2", TaskStatus.NEW);
    }

    @Test
    void addTaskWithGeneratedId() {
        taskManager.addTask(task1);
        List<Task> tasks = taskManager.getTask();
        assertEquals(task1, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void addTaskWithGivenId() {
        Task task = new Task("task", "test", TaskStatus.NEW, 2);
        taskManager.addTask(task);
        Task taskTest = taskManager.getTaskByID(2);
        assertEquals(task, taskTest);
    }


    @Test
    void addSubtaskWithGeneratedId() {
        Epic epic = new Epic("epic", "test");
        taskManager.addEpic(epic);
        int epicId = taskManager.getId(epic);
        Subtask subtask = new Subtask("subtask", "test", TaskStatus.NEW, epicId);
        taskManager.addSubtask(subtask);
        int subtaskID = taskManager.getId(subtask);
        assertEquals(subtask, taskManager.getSubtaskByID(subtaskID));
    }

    @Test
    void addSubtaskWithGivenId() {
        Epic epic = new Epic("epic", "test");
        taskManager.addEpic(epic);
        int epicId = taskManager.getId(epic);
        Subtask subtask = new Subtask("subtask", "test", TaskStatus.NEW, epicId, 2);
        taskManager.addSubtask(subtask);
        assertEquals(subtask, taskManager.getSubtaskByID(2));
    }

    @Test
    void addEpicWithGeneratedId() {
        Epic epic = new Epic("epic", "test");
        taskManager.addEpic(epic);
        int epicId = taskManager.getId(epic);
        assertEquals(epic, taskManager.getEpicByID(epicId));
    }

    @Test
    void addEpicWithGivenId() {
        Epic epic = new Epic("epic", "test", 4);
        taskManager.addEpic(epic);
        assertEquals(epic, taskManager.getEpicByID(4));
    }

    @Test
    void updateTask() {
        TaskManager taskManager = new InMemoryTaskManager();
        taskManager.addTask(task1);
        int id = taskManager.getId(task1);
        Task task2 = new Task("taskTest2", "test2", TaskStatus.IN_PROGRESS, id);
        taskManager.updateTask(task2);
        assertEquals(task2, taskManager.getTaskByID(id));
    }

    @Test
    void updateSubtask() {
        Subtask subtask1 = new Subtask("subtaskTest", "test", TaskStatus.NEW);
        ArrayList<Subtask> subtasks = new ArrayList<>();
        subtasks.add(subtask1);
        Epic epic = new Epic("epicName", "test", subtasks);
        TaskManager taskManager = new InMemoryTaskManager();
        taskManager.addEpic(epic);
        int epicId = taskManager.getId(epic);
        int subtaskId = taskManager.getId(subtask1);
        Subtask updateSubtask = new Subtask("subtaskTest2", "test2", TaskStatus.IN_PROGRESS, epicId, subtaskId);
        taskManager.updateSubtask(updateSubtask);
        assertEquals(updateSubtask, taskManager.getSubtaskByID(subtaskId));
    }

    @Test
    void updateEpic() {
        Subtask subtask1 = new Subtask("subtaskTest", "test", TaskStatus.NEW);
        Subtask subtask2 = new Subtask("subtaskTest2", "test2", TaskStatus.IN_PROGRESS);
        ArrayList<Subtask> subtasks1 = new ArrayList<>();
        ArrayList<Subtask> subtasks2 = new ArrayList<>();
        subtasks1.add(subtask1);
        subtasks2.add(subtask2);
        Epic epic1 = new Epic("epicName", "test", subtasks1);
        taskManager.addEpic(epic1);
        int id = taskManager.getId(epic1);
        Epic epic2 = new Epic("epicName", "test", subtasks2, id);
        taskManager.updateEpic(epic2);
        assertEquals(epic2, taskManager.getEpicByID(id));
    }

    @Test
    void getTask() {
        taskManager.addTask(task1);
        List<Task> tasks1 = taskManager.getTask();
        List<Task> tasks2 = new ArrayList<>();
        tasks2.add(task1);
        assertEquals(tasks2, tasks1, "Задачи не совпадают.");
    }

    @Test
    void getEpic() {
        Subtask subtask1 = new Subtask("subtaskTest", "test", TaskStatus.NEW);
        Subtask subtask2 = new Subtask("subtaskTest2", "test2", TaskStatus.IN_PROGRESS);
        ArrayList<Subtask> subtaskArrayList = new ArrayList<>();
        subtaskArrayList.add(subtask1);
        subtaskArrayList.add(subtask2);
        Epic epic = new Epic("epicTest", "Test", subtaskArrayList);
        taskManager.addEpic(epic);
        List<Epic> epics1 = new ArrayList<>();
        epics1.add(epic);
        List<Epic> epics2 = taskManager.getEpic();
        assertEquals(epics1, epics2, "Задачи не совпадают.");
    }

    @Test
    void getTaskByID() {
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        int id = taskManager.getId(task2);
        Task task2Test = taskManager.getTaskByID(id);
        assertEquals(task2, task2Test);
    }

    @Test
    void getSubtaskByID() {
        Subtask subtask1 = new Subtask("subtaskTest", "test", TaskStatus.NEW);
        Subtask subtask2 = new Subtask("subtaskTest2", "test2", TaskStatus.IN_PROGRESS);
        ArrayList<Subtask> subtasks = new ArrayList<>();
        subtasks.add(subtask1);
        subtasks.add(subtask2);
        Epic epic = new Epic("epicName", "test", subtasks);
        taskManager.addEpic(epic);
        int id = taskManager.getId(subtask2);
        assertEquals(subtask2, taskManager.getSubtaskByID(id));
    }

    @Test
    void getEpicByID() {
        Epic epic1 = new Epic("epicTest1", "Test");
        Epic epic2 = new Epic("epicTest2", "Test");
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        int id = taskManager.getId(epic2);
        assertEquals(epic2,taskManager.getEpicByID(id));
    }

    @Test
    void getHistory() {
        Task task = new Task("task", "test", TaskStatus.NEW, 1);
        taskManager.addTask(task);
        taskManager.getTaskByID(1);
        List<Task> history = new ArrayList<>();
        history.add(task);

        List<Task> historyTest = taskManager.getHistory();
        assertEquals(history, historyTest);
    }

    @Test
    void clearEpic() {
        Epic epic = new Epic("epic", "test", 1);
        Subtask subtask = new Subtask("subtask", "test", TaskStatus.NEW, 1);
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask);
        taskManager.clearSubtask();
        taskManager.clearEpic();
        assertTrue(taskManager.getSubtask().isEmpty());
        assertTrue(taskManager.getEpic().isEmpty());
    }

    @Test
    void removeEpicByID() {
        Epic epic = new Epic("epic", "test", 1);
        Subtask subtask = new Subtask("subtask", "test", TaskStatus.NEW, 1, 2);
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask);
        taskManager.removeEpicByID(1);
        assertTrue(taskManager.getEpic().isEmpty());
        assertTrue(taskManager.getSubtask().isEmpty());
    }

    @Test
    void removeSubtaskByID() {
        Epic epic = new Epic("epic", "test", 1);
        Subtask subtask = new Subtask("subtask", "test", TaskStatus.NEW, 1, 2);
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask);
        taskManager.removeSubtaskByID(2);
        assertTrue(taskManager.getSubtask().isEmpty());
        Epic epicTest = taskManager.getEpicByID(1);
        ArrayList<Subtask> subtasks = epicTest.getSubtasks();
        assertTrue(subtasks.isEmpty());
    }
}