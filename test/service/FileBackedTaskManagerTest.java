package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanagement.Epic;
import taskmanagement.Subtask;
import taskmanagement.Task;
import taskmanagement.TaskStatus;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileBackedTaskManagerTest {
    private File file;
    private FileBackedTaskManager taskManager;

    @BeforeEach
    public void setUp() throws IOException {
        file = File.createTempFile("tasks", ".csv");
        taskManager = new FileBackedTaskManager(file);
    }

    @Test
    public void testSaveAndLoadEmptyFile() {
        taskManager = FileBackedTaskManager.loadFromFile(file);
        assertTrue(taskManager.getTask().isEmpty());
        assertTrue(taskManager.getEpic().isEmpty());
        assertTrue(taskManager.getSubtask().isEmpty());
    }

    @Test
    public void testSaveAndLoadTasks() {
        Task task1 = new Task("Задача 1", "Описание 1", TaskStatus.NEW);
        taskManager.addTask(task1);

        Task task2 = new Task("Задача 2", "Описание 2", TaskStatus.IN_PROGRESS);
        taskManager.addTask(task2);

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.addEpic(epic1);

        Subtask Subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", TaskStatus.NEW, epic1.getId());
        taskManager.addSubtask(Subtask1);

        taskManager = FileBackedTaskManager.loadFromFile(file);

        List<Task> tasks = taskManager.getTask();
        assertEquals(2, tasks.size());
        assertEquals("Задача 1", tasks.get(0).getName());
        assertEquals("Задача 2", tasks.get(1).getName());

        List<Epic> epics = taskManager.getEpic();
        assertEquals(1, epics.size());
        assertEquals("Эпик 1", epics.get(0).getName());

        List<Subtask> Subtasks = taskManager.getSubtask();
        assertEquals(1, Subtasks.size());
        assertEquals("Подзадача 1", Subtasks.get(0).getName());
        assertEquals(epic1.getId(), Subtasks.get(0).getEpicId());
    }

    @Test
    public void testSaveAndLoadTasksWithTime() {
        LocalDateTime time1 = LocalDateTime.of(2024, 10, 10, 8, 0);
        LocalDateTime time2 = LocalDateTime.of(2024, 10, 10, 9, 0);
        LocalDateTime time3 = LocalDateTime.of(2024, 10, 10, 10, 0);
        Duration duration = Duration.ofMinutes(30);

        Task task1 = new Task("Задача 1", "Описание 1", TaskStatus.NEW, duration, time1);
        taskManager.addTask(task1);

        Task task2 = new Task("Задача 2", "Описание 2", TaskStatus.IN_PROGRESS, duration, time2);
        taskManager.addTask(task2);

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.addEpic(epic1);

        Subtask Subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", TaskStatus.NEW, epic1.getId(), duration, time3);
        taskManager.addSubtask(Subtask1);

        taskManager = FileBackedTaskManager.loadFromFile(file);

        List<Task> tasks = taskManager.getTask();
        assertEquals(2, tasks.size());
        assertEquals("Задача 1", tasks.get(0).getName());
        assertEquals("Задача 2", tasks.get(1).getName());

        List<Epic> epics = taskManager.getEpic();
        assertEquals(1, epics.size());
        assertEquals("Эпик 1", epics.get(0).getName());

        List<Subtask> Subtasks = taskManager.getSubtask();
        assertEquals(1, Subtasks.size());
        assertEquals("Подзадача 1", Subtasks.get(0).getName());
        assertEquals(epic1.getId(), Subtasks.get(0).getEpicId());
    }

    @Test
    public void testSaveAndLoadMultipleTasks() {
        Task task1 = new Task("Задание 1", "Описание 1", TaskStatus.NEW);
        taskManager.addTask(task1);
        int id1 = task1.getId();

        Task task2 = new Task("Задание 2", "Описание 2", TaskStatus.IN_PROGRESS);
        taskManager.addTask(task2);
        int id2 = task2.getId();

        taskManager = FileBackedTaskManager.loadFromFile(file);

        List<Task> tasks = taskManager.getTask();
        assertEquals(task1, taskManager.getTaskByID(id1));
        assertEquals(task2, taskManager.getTaskByID(id2));


    }
}
