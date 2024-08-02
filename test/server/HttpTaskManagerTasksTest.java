package server;

import com.google.gson.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import service.InMemoryTaskManager;
import service.TaskManager;
import service.TaskNotFoundException;
import taskmanagement.Task;
import taskmanagement.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerTasksTest {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    Task task = new Task("Test 2", "Testing task 2",
            TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskManagerTasksTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.clearTask();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getTask();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetTasks() throws IOException, InterruptedException {
        manager.addTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request2, HttpResponse.BodyHandlers.ofString());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonArray jsonArray = jsonElement.getAsJsonArray();

        JsonObject object = jsonArray.get(0).getAsJsonObject();
        String name = object.get("name").getAsString();
        String description = object.get("description").getAsString();
        String statusStr = object.get("status").getAsString();
        int durationInt = object.get("duration").getAsInt();
        String startTimeStr = object.get("startTime").getAsString();
        int id = object.get("id").getAsInt();

        LocalDateTime startTime = LocalDateTime.parse(startTimeStr, formatter);
        Duration duration = Duration.ofMinutes(durationInt);
        TaskStatus status = TaskStatus.valueOf(statusStr);
        List<Task> tasksFromManager = manager.getTask();

        Task taskTest = new Task(name, description, status, duration, startTime, id);
        assertEquals(taskTest, tasksFromManager.get(0));
        assertEquals(200, response.statusCode());
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        task.setId(1);
        manager.addTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject object = jsonElement.getAsJsonObject();

        String name = object.get("name").getAsString();
        String description = object.get("description").getAsString();
        String statusStr = object.get("status").getAsString();
        int durationInt = object.get("duration").getAsInt();
        String startTimeStr = object.get("startTime").getAsString();
        int id = object.get("id").getAsInt();

        LocalDateTime startTime = LocalDateTime.parse(startTimeStr, formatter);
        Duration duration = Duration.ofMinutes(durationInt);
        TaskStatus status = TaskStatus.valueOf(statusStr);

        Task taskTest = new Task(name, description, status, duration, startTime, id);

        assertEquals(manager.getTaskByID(1), taskTest);
        assertEquals(200, response.statusCode());
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        task.setId(1);
        Task task2 = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, Duration.ofMinutes(5),
                LocalDateTime.now().plus(Duration.ofMinutes(30)), 2);
        manager.addTask(task);
        manager.addTask(task2);

        assertEquals(task, manager.getTaskByID(1));
        assertEquals(task2, manager.getTaskByID(2));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/2");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(1, manager.getTask().size());
        assertEquals(task, manager.getTaskByID(1));
        assertThrows(TaskNotFoundException.class, () -> {
            manager.getTaskByID(2);
        });
        assertEquals(201, response.statusCode());
    }

}
