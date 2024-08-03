package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.TaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;

    public HistoryHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");

        if (method.equals("GET") && pathParts.length == 2 && pathParts[1].equals("history")) {
            handleGetHistory(exchange);
        } else {
            writeResponse(exchange, "Not Found", 404);
        }
    }

    private void handleGetHistory(HttpExchange exchange) throws IOException {
        String responseString = gson.toJson(manager.getHistory());
        writeResponse(exchange, responseString, 200);
    }
}
