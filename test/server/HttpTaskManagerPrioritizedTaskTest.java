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
import taskmanagement.Epic;
import taskmanagement.Subtask;
import taskmanagement.Task;
import taskmanagement.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskManagerPrioritizedTaskTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskManagerPrioritizedTaskTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.clearTask();
        manager.clearSubtask();
        manager.clearEpic();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testGetHistory() throws IOException, InterruptedException {
        Task task1 = new Task("task1", "test", TaskStatus.NEW,
                Duration.ofMinutes(30), LocalDateTime.now(), 1);
        Epic epic = new Epic("epic1", "test", 2);
        Subtask subtask = new Subtask("subtask1", "test", TaskStatus.NEW, 2, 3,
                Duration.ofMinutes(30),
                LocalDateTime.now().plus(Duration.ofMinutes(60)));
        Task task2 = new Task("task2", "test", TaskStatus.NEW, 4);
        manager.addEpic(epic);
        manager.addTask(task1);
        manager.addSubtask(subtask);
        manager.addTask(task2);

        manager.getTaskByID(4);
        manager.getSubtaskByID(3);
        manager.getTaskByID(1);
        manager.getEpicByID(2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonElement jsonElement = JsonParser.parseString(response.body());

        JsonObject object1 = jsonElement.getAsJsonArray().get(0).getAsJsonObject();
        String name1 = object1.get("name").getAsString();

        JsonObject object2 = jsonElement.getAsJsonArray().get(1).getAsJsonObject();
        String name2 = object2.get("name").getAsString();

        JsonObject object3 = jsonElement.getAsJsonArray().get(2).getAsJsonObject();
        String name3 = object3.get("name").getAsString();

        assertEquals(200, response.statusCode());
        assertEquals("task1", name1);
        assertEquals("subtask1", name2);
        assertEquals("task2", name3);
    }
}
