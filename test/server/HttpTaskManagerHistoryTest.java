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

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskManagerHistoryTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskManagerHistoryTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.clearTask();
        manager.clearEpic();
        manager.clearSubtask();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testGetHistory() throws IOException, InterruptedException {
        Task task1 = new Task("task1", "test", TaskStatus.NEW, 1);
        Epic epic = new Epic("epic1", "test", 2);
        Subtask subtask = new Subtask("subtask1", "test", TaskStatus.NEW, 2, 3);
        manager.addEpic(epic);
        manager.addTask(task1);
        manager.addSubtask(subtask);

        manager.getTaskByID(1);
        manager.getSubtaskByID(3);
        manager.getEpicByID(2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
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
        assertEquals("epic1", name3);
    }
}
