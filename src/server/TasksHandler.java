package server;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.TaskManager;

import service.TaskNotFoundException;
import service.TaskOverlapException;
import taskmanagement.Task;
import taskmanagement.TaskStatus;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;

    public TasksHandler(TaskManager manager) {
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
                handleGetTasks(exchange);
                return;
            case POST_TASK:
                handlePostTask(exchange);
                return;
            case GET_TASK_BY_ID:
                handleGetTaskById(exchange, pathParts);
                return;
            case DELETE_TASK:
                handleDeleteTaskById(exchange, pathParts);
                return;
            default:
                writeResponse(exchange, "Not Found", 404);
        }
    }

    private TaskEndpoint getEndpoint(String method, String[] pathParts) {
        if (pathParts.length == 2 && pathParts[1].equals("tasks")) {
            if (method.equals("GET")) {
                return TaskEndpoint.GET_TASKS;
            }
            if (method.equals("POST")) {
                return TaskEndpoint.POST_TASK;
            }
        }
        if (pathParts.length == 3 && pathParts[1].equals("tasks")) {
            if (method.equals("GET")) {
                return TaskEndpoint.GET_TASK_BY_ID;
            }
            if (method.equals("DELETE")) {
                return TaskEndpoint.DELETE_TASK;
            }
        }
        return TaskEndpoint.UNKNOWN;
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        String responseString = gson.toJson(manager.getTask());
        writeResponse(exchange, responseString,  200);
    }

    private void handlePostTask(HttpExchange exchange) throws IOException {
        JsonObject jsonObject = createJsonObject(exchange);

        if (jsonObject.has("id") && jsonObject.get("id").getAsInt() != 0) {
            handleUpdateTask(exchange, jsonObject);
            return;
        }
        Task task = createTaskObject(jsonObject);
        try {
            manager.addTask(task);
            writeResponse(exchange, "задача успешно добавлена", 201);
        } catch (TaskOverlapException e) {
            writeResponse(exchange, "Not Acceptable", 406);
        }
    }

    private void handleUpdateTask(HttpExchange exchange, JsonObject jsonObject) throws IOException {
        Task task = createTaskObject(jsonObject);
        task.setId(jsonObject.get("id").getAsInt());
        try {
            manager.updateTask(task);
        } catch (TaskOverlapException e) {
            writeResponse(exchange, "Not Acceptable", 406);
        }
        writeResponse(exchange, "задача успешно обновлена", 201);
    }

    private void handleGetTaskById(HttpExchange exchange, String[] pathParts) throws IOException {
        int id = Integer.parseInt(pathParts[2]);
        try {
            Task task = manager.getTaskByID(id);
            String responseString = gson.toJson(task);
            writeResponse(exchange, responseString, 200);
        } catch (TaskNotFoundException e) {
            writeResponse(exchange, "Not Found", 404);
        }
    }

    private void handleDeleteTaskById(HttpExchange exchange, String[] pathParts) throws IOException {
        int id = Integer.parseInt(pathParts[2]);
        try {
            manager.removeTaskByID(id);
            writeResponse(exchange, "задача успешно удалена", 201);
        } catch (TaskNotFoundException e) {
            writeResponse(exchange, "Not Found", 404);
        }
    }

    private Task createTaskObject(JsonObject jsonObject) {
        String name = jsonObject.get("name").getAsString();
        String description = jsonObject.get("description").getAsString();
        String statusStr = jsonObject.get("status").getAsString();
        TaskStatus status = TaskStatus.valueOf(statusStr);

        if (jsonObject.has("duration") && jsonObject.has("startTime")) {
            String startTimeStr = jsonObject.get("startTime").getAsString();
            int durationInt = jsonObject.get("duration").getAsInt();
            LocalDateTime startTime = LocalDateTime.parse(startTimeStr, formatter);
            Duration duration = Duration.ofMinutes(durationInt);
            return new Task(name, description, status, duration, startTime);
        }
        return new Task(name, description, status);
    }
}
