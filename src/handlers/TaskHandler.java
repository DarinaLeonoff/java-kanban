package handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import model.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class TaskHandler extends BaseHandler {
    private TaskManager manager;

    public TaskHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        super.handle(exchange);

        String response = "";
        String[] pathArray = path.split("/");
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(Optional.class, new OptionalAdapter())
                .create();
        switch (method) {
            case "GET":
                if (pathArray.length == 2) {
                    response = gson.toJson(manager.getTasks(), new TypeToken<List<Task>>() {
                    }.getType());
                    sendText(exchange, 200, response);
                } else if (pathArray.length == 3) {
                    try {
                        response = gson.toJson(manager.getTaskById(Integer.parseInt(pathArray[2])));
                        sendText(exchange, 200, response);
                    } catch (Exception e) {
                        sendNotFound(exchange);
                    }
                }
                break;
            case "POST":
                try {
                    Task task = gson.fromJson(body, Task.class);
                    if (pathArray.length == 2) {
                        manager.createTask(task);
                        response = "Task added";
                        sendText(exchange, 201, response);
                    } else if (pathArray.length == 3) {
                        manager.updateTask(task);
                        response = "Task updated";
                        sendText(exchange, 201, response);
                    }
                } catch (Exception e) {
                    sendHasInteractions(exchange);
                }
                break;
            case "DELETE":
                manager.removeTask(Integer.parseInt(pathArray[2]));
                sendText(exchange, 200, "Delete completed");
                break;
            default:
                exchange.sendResponseHeaders(exchange.getResponseCode(), 0);
        }
    }


}
