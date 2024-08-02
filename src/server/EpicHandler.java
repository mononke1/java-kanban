package server;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.TaskManager;
import service.TaskNotFoundException;
import service.TaskOverlapException;
import taskmanagement.Epic;
import taskmanagement.Subtask;
import taskmanagement.TaskStatus;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;

    public EpicHandler(TaskManager manager) {
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
                handleGetEpics(exchange);
                return;
            case POST_TASK:
                handlePostEpic(exchange);
                return;
            case GET_TASK_BY_ID:
                handleGetEpicById(exchange, pathParts);
                return;
            case DELETE_TASK:
                handleDeleteEpicById(exchange, pathParts);
                return;
            case GET_EPIC_SUBTASKS:
                handleGetEpicSubtasks(exchange, pathParts);
            default:
                writeResponse(exchange, "Not Found", 404);
        }
    }

    private TaskEndpoint getEndpoint(String method, String[] pathParts) {
        if (pathParts.length == 2 && pathParts[1].equals("epics")) {
            if (method.equals("GET")) {
                return TaskEndpoint.GET_TASKS;
            }
            if (method.equals("POST")) {
                return TaskEndpoint.POST_TASK;
            }
        }
        if (pathParts.length == 3 && pathParts[1].equals("epics")) {
            if (method.equals("GET")) {
                return TaskEndpoint.GET_TASK_BY_ID;
            }
            if (method.equals("DELETE")) {
                return TaskEndpoint.DELETE_TASK;
            }
        }
        if (pathParts.length == 4 && pathParts[1].equals("epics") && pathParts[3].equals("subtasks")) {
            if (method.equals("GET")) {
                return TaskEndpoint.GET_EPIC_SUBTASKS;
            }
        }
        return TaskEndpoint.UNKNOWN;
    }

    private void handleGetEpics(HttpExchange exchange) throws IOException {
        String responseString = gson.toJson(manager.getEpic());
        writeResponse(exchange, responseString,  200);
    }

    private void handleGetEpicSubtasks(HttpExchange exchange, String[] pathParts) throws IOException {
        int id = Integer.parseInt(pathParts[2]);
        String responseString = gson.toJson(manager.getEpicSubtasks(id));
        writeResponse(exchange, responseString,  200);
    }

    private void handlePostEpic(HttpExchange exchange) throws IOException {
        JsonObject jsonObject = createJsonObject(exchange);

        if (jsonObject.has("id") && jsonObject.get("id").getAsInt() != 0) {
            handleUpdateEpic(exchange, jsonObject);
            return;
        }
        Epic epic = createEpicObject(exchange, jsonObject);
        try {
            manager.addEpic(epic);
            writeResponse(exchange, "задача успешно добавлена", 201);
        } catch (TaskOverlapException e) {
            writeResponse(exchange, "Not Acceptable", 406);
        }
    }

    private void handleUpdateEpic(HttpExchange exchange, JsonObject jsonObject) throws IOException {
        Epic epic = createEpicObject(exchange, jsonObject);
        epic.setId(jsonObject.get("id").getAsInt());
        try {
            manager.updateEpic(epic);
        } catch (TaskOverlapException e) {
            writeResponse(exchange, "Not Acceptable", 406);
        }
        writeResponse(exchange, "задача успешно обновлена", 201);
    }

    private void handleGetEpicById(HttpExchange exchange, String[] pathParts) throws IOException {
        int id = Integer.parseInt(pathParts[2]);
        try {
            Epic epic = manager.getEpicByID(id);
            String responseString = gson.toJson(epic);
            writeResponse(exchange, responseString, 200);
        } catch (TaskNotFoundException e) {
            writeResponse(exchange, "Not Found", 404);
        }
    }

    private void handleDeleteEpicById(HttpExchange exchange, String[] pathParts) throws IOException {
        int id = Integer.parseInt(pathParts[2]);
        try {
            manager.removeEpicByID(id);
            writeResponse(exchange, "задача успешно удалена", 201);
        } catch (TaskNotFoundException e) {
            writeResponse(exchange, "Not Found", 404);
        }
    }

    private Epic createEpicObject(HttpExchange exchange,JsonObject jsonObject) throws IOException {
        String name = jsonObject.get("name").getAsString();
        String description = jsonObject.get("description").getAsString();
        ArrayList<Subtask> subtasks = new ArrayList<>();

        if (jsonObject.has("subtasks")) {
            JsonElement jsonElement = jsonObject.get("subtasks");
            if(!jsonElement.isJsonArray()) {
                writeResponse(exchange, "Invalid JSON format", 400);
            }
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            Subtask subtask;
            for (JsonElement element : jsonArray) {
                JsonObject object = element.getAsJsonObject();

                String subtaskName = object.get("name").getAsString();
                String subtaskDescription = object.get("description").getAsString();
                String statusStr = object.get("status").getAsString();
                TaskStatus status = TaskStatus.valueOf(statusStr);

                if (object.has("duration") && object.has("startTime")) {
                    String startTimeStr = object.get("startTime").getAsString();
                    int durationInt = object.get("duration").getAsInt();
                    LocalDateTime startTime = LocalDateTime.parse(startTimeStr, formatter);
                    Duration duration = Duration.ofMinutes(durationInt);
                    subtask = new Subtask(subtaskName, subtaskDescription, status, duration, startTime);
                } else {
                    subtask = new Subtask(subtaskName, subtaskDescription, status);
                }
                subtasks.add(subtask);
            }
        }
        return new Epic(name, description, subtasks);
    }
}
