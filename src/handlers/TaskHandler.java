package handlers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import model.Task;

import java.io.IOException;
import java.util.List;

public class TaskHandler extends BaseHandler {
    public TaskHandler(TaskManager manager, Gson gson) {
        super(manager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        super.handle(exchange);

        switch (exchange.getRequestMethod()) {
            case "GET":
                handleGet(exchange);
                break;
            case "POST":
                handlePost(exchange);
                break;
            case "DELETE":
                handleDelete(exchange);
                break;
            default:
                exchange.sendResponseHeaders(exchange.getResponseCode(), 0);
        }
    }

    private void handleGet(HttpExchange ex) throws IOException {
        if (pathArray.length == 2) {
            sendText(ex, 200, gson.toJson(manager.getTasks(), new TypeToken<List<Task>>() {
            }.getType()));
        } else if (pathArray.length == 3) {
            try {
                sendText(ex, 200, gson.toJson(manager.getTaskById(Integer.parseInt(pathArray[2]))));
            } catch (Exception e) {
                sendNotFound(ex);
            }
        }
    }

    private void handlePost(HttpExchange ex) throws IOException {
        try {
            System.out.println(body);
            Task task = gson.fromJson(body, Task.class);
            if (pathArray.length == 2) {
                manager.createTask(task);
                sendText(ex, 201, "Task added");
            } else if (pathArray.length == 3) {
                manager.updateTask(task);
                sendText(ex, 201, "Task updated");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            sendHasInteractions(ex);
        }
    }

    private void handleDelete(HttpExchange ex) throws IOException {
        manager.removeTask(Integer.parseInt(pathArray[2]));
        sendText(ex, 200, "Delete completed");
    }

}
