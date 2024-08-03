package server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;
import service.TaskManager;
import service.TaskNotFoundException;
import taskmanagement.Epic;
import taskmanagement.Subtask;
import taskmanagement.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerSubtaskTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskManagerSubtaskTest() throws IOException {
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
    public void testAddSubtask() throws IOException, InterruptedException {
        Subtask subtask = new Subtask("subtask", "test", TaskStatus.NEW, 1,
                Duration.ofMinutes(5), LocalDateTime.now());
        Epic epic = new Epic("epic", "test", 1);
        manager.addEpic(epic);

        String taskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Subtask> tasksFromManager = manager.getSubtask();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("subtask", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetSubtask() throws IOException, InterruptedException {
        Subtask subtask = new Subtask("subtask", "test", TaskStatus.NEW);
        ArrayList<Subtask> subtasks = new ArrayList<>();
        subtasks.add(subtask);
        Epic epic = new Epic("epic", "test", subtasks,1);
        manager.addEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject object = jsonElement.getAsJsonArray().get(0).getAsJsonObject();
        String name = object.get("name").getAsString();
        String description = object.get("description").getAsString();
        TaskStatus status = TaskStatus.valueOf(object.get("status").getAsString());
        int epicId  = object.get("epicId").getAsInt();
        int id = object.get("id").getAsInt();

        Subtask subtaskTest = new Subtask(name, description, status, epicId, id);

        assertEquals(200, response.statusCode());
        assertEquals(manager.getSubtask().get(0), subtaskTest);
    }

    @Test
    public void testGetSubtaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "test", 1);
        manager.addEpic(epic);
        Subtask subtask = new Subtask("subtask", "test", TaskStatus.NEW, 1, 2);
        manager.addSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject object = jsonElement.getAsJsonObject();
        String name = object.get("name").getAsString();
        String description = object.get("description").getAsString();
        TaskStatus status = TaskStatus.valueOf(object.get("status").getAsString());
        int id = object.get("id").getAsInt();
        int epicId = object.get("epicId").getAsInt();

        Subtask subtaskTest = new Subtask(name, description, status, epicId, id);

        assertEquals(200, response.statusCode());
        assertEquals(manager.getSubtaskByID(2), subtaskTest);
    }

    @Test
    public void testDeleteSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "test", 1);
        manager.addEpic(epic);
        Subtask subtask = new Subtask("subtask", "test", TaskStatus.NEW, 1, 2);
        manager.addSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertThrows(TaskNotFoundException.class, () -> {
            manager.getSubtaskByID(2);
        });
        assertEquals(0, manager.getSubtask().size());
    }
}
