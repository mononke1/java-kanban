package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.TaskManager;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    TaskManager manager;

    public PrioritizedHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");

        if (method.equals("GET") && pathParts.length == 2 && pathParts[1].equals("prioritized")) {
            handleGetPrioritized(exchange);
        } else {
            writeResponse(exchange, "Not Found", 404);
        }
    }

    private void handleGetPrioritized(HttpExchange exchange) throws IOException {
        String responseString = gson.toJson(manager.getPrioritizedTasks());
        writeResponse(exchange, responseString, 200);
    }
}
