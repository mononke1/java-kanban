package server;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.TaskManager;
import service.TaskNotFoundException;
import service.TaskOverlapException;
import taskmanagement.Subtask;
import taskmanagement.TaskStatus;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;

    public SubtaskHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");
        TaskEndpoint endpoint = getEndpoint(method, pathParts);

        switch (endpoint) {
            case GET_TASKS:
                handleGetSubtask(exchange);
                return;
            case POST_TASK:
                handlePostSubtask(exchange);
                return;
            case GET_TASK_BY_ID:
                handleGetSubtaskById(exchange, pathParts);
                return;
            case DELETE_TASK:
                handleDeleteSubtaskById(exchange, pathParts);
                return;
            default:
                writeResponse(exchange, "Not Found", 404);
        }
    }

    private TaskEndpoint getEndpoint(String method, String[] pathParts) {
        if (pathParts.length == 2 && pathParts[1].equals("subtasks")) {
            if (method.equals("GET")) {
                return TaskEndpoint.GET_TASKS;
            }
            if (method.equals("POST")) {
                return TaskEndpoint.POST_TASK;
            }
        }
        if (pathParts.length == 3 && pathParts[1].equals("subtasks")) {
            if (method.equals("GET")) {
                return TaskEndpoint.GET_TASK_BY_ID;
            }
            if (method.equals("DELETE")) {
                return TaskEndpoint.DELETE_TASK;
            }
        }
        return TaskEndpoint.UNKNOWN;
    }

    private void handleGetSubtask(HttpExchange exchange) throws IOException {
        String responseString = gson.toJson(manager.getSubtask());
        writeResponse(exchange, responseString, 200);
    }

    private void handlePostSubtask(HttpExchange exchange) throws IOException {
        JsonObject jsonObject = createJsonObject(exchange);

        if (jsonObject.has("id") && jsonObject.get("id").getAsInt() != 0) {
            handleUpdateSubtask(exchange, jsonObject);
            return;
        }
        Subtask subtask = createSubtaskObject(jsonObject);
        try {
            manager.addSubtask(subtask);
            writeResponse(exchange, "задача успешно добавлена", 201);
        } catch (TaskOverlapException e) {
            writeResponse(exchange, "Not Acceptable", 406);
        }
    }

    private void handleUpdateSubtask(HttpExchange exchange, JsonObject jsonObject) throws IOException {
        Subtask subtask = createSubtaskObject(jsonObject);
        subtask.setId(jsonObject.get("id").getAsInt());
        try {
            manager.updateSubtask(subtask);
        } catch (TaskOverlapException e) {
            writeResponse(exchange, "Not Acceptable", 406);
        }
        writeResponse(exchange, "задача успешно обновлена", 201);
    }

    private void handleGetSubtaskById(HttpExchange exchange, String[] pathParts) throws IOException {
        int id = Integer.parseInt(pathParts[2]);
        try {
            Subtask subtask = manager.getSubtaskByID(id);
            String responseString = gson.toJson(subtask);
            writeResponse(exchange, responseString, 200);
        } catch (TaskNotFoundException e) {
            writeResponse(exchange, "Not Found", 404);
        }
    }

    private void handleDeleteSubtaskById(HttpExchange exchange, String[] pathParts) throws IOException {
        int id = Integer.parseInt(pathParts[2]);
        try {
            manager.removeSubtaskByID(id);
            writeResponse(exchange, "задача успешно удалена", 201);
        } catch (TaskNotFoundException e) {
            writeResponse(exchange, "Not Found", 404);
        }
    }

    private Subtask createSubtaskObject(JsonObject jsonObject) {
        String name = jsonObject.get("name").getAsString();
        String description = jsonObject.get("description").getAsString();
        String statusStr = jsonObject.get("status").getAsString();
        TaskStatus status = TaskStatus.valueOf(statusStr);
        int epicId = jsonObject.get("epicId").getAsInt();

        if (jsonObject.has("duration") && jsonObject.has("startTime")) {
            String startTimeStr = jsonObject.get("startTime").getAsString();
            int durationInt = jsonObject.get("duration").getAsInt();
            LocalDateTime startTime = LocalDateTime.parse(startTimeStr, FORMATTER);
            Duration duration = Duration.ofMinutes(durationInt);
            return new Subtask(name, description, status, epicId, duration, startTime);
        }
        return new Subtask(name, description, status, epicId);
    }
}
