package server;

import com.google.gson.*;
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

public class HttpTaskManagerEpicTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskManagerEpicTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.clearSubtask();
        manager.clearEpic();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        Subtask subtask = new Subtask("subtask", "test", TaskStatus.NEW,
                Duration.ofMinutes(5), LocalDateTime.now());

        ArrayList<Subtask> subtasks = new ArrayList<>();
        subtasks.add(subtask);
        Epic epic = new Epic("epic", "test", subtasks);
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Epic> tasksFromManager = manager.getEpic();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("epic", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetEpic() throws IOException, InterruptedException {
        Subtask subtask = new Subtask("subtask", "test", TaskStatus.NEW);

        ArrayList<Subtask> subtasks = new ArrayList<>();
        subtasks.add(subtask);
        Epic epic = new Epic("epic", "test", subtasks);
        manager.addEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        JsonObject object = jsonArray.get(0).getAsJsonObject();
        String name = object.get("name").getAsString();
        String description = object.get("description").getAsString();
        int id = object.get("id").getAsInt();
        TaskStatus epicStatus = TaskStatus.valueOf(object.get("status").getAsString());
        JsonElement element = object.get("subtasks");
        JsonObject jsonObject = element.getAsJsonArray().get(0).getAsJsonObject();
        String subtaskName = jsonObject.get("name").getAsString();
        String subtaskDescription = jsonObject.get("description").getAsString();
        TaskStatus SubtaskStatus = TaskStatus.valueOf(jsonObject.get("status").getAsString());
        int subtaskId = jsonObject.get("id").getAsInt();

        Subtask subtaskTest = new Subtask(subtaskName, subtaskDescription, SubtaskStatus, id, subtaskId);
        ArrayList<Subtask> subtasks1 = new ArrayList<>();
        subtasks1.add(subtaskTest);

        Epic epic1 = new Epic(name, description, subtasks1, id);
        epic1.setStatus(epicStatus);

        assertEquals(manager.getEpic().get(0), epic1);
        assertEquals(200, response.statusCode());
    }

    @Test
    public void testGetEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "test", 1);
        manager.addEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
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

        Epic epicTest = new Epic(name, description, id);
        epicTest.setStatus(status);
        assertEquals(manager.getEpicByID(1), epicTest);
        assertEquals(200, response.statusCode());
    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "test", 1);
        manager.addEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


        assertThrows(TaskNotFoundException.class, () -> {
            manager.getEpicByID(1);
        });
        assertEquals(0, manager.getEpic().size());
        assertEquals(201, response.statusCode());
    }
}
