package handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import model.Status;
import model.Subtask;
import model.Task;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

public class SubtaskHandler extends BaseHandler {
    private TaskManager manager;

    public SubtaskHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        super.handle(exchange);
        Gson gson = new GsonBuilder().registerTypeAdapter(Duration.class, new DurationAdapter()).registerTypeAdapter(Optional.class, new OptionalAdapter()).create();
        String response = "";
        String[] pathArray = super.path.split("/");
        switch (method) {
            case "GET":
                if (pathArray.length == 2) {
                    response = gson.toJson(manager.getSubtasks(), new TypeToken<List<Subtask>>() {
                    }.getType());
                    sendText(exchange, 200, response);
                } else if (pathArray.length == 3) {
                    try {
                        response = gson.toJson(manager.getSubtaskById(Integer.parseInt(pathArray[2])));
                        sendText(exchange, 200, response);
                    } catch (NumberFormatException e) {
                        sendNotFound(exchange);
                    }
                }
                break;
            case "POST":
                try {
                    Subtask subtask = gson.fromJson(body, Subtask.class);
                    if (pathArray.length == 2) {
                        manager.createSubtask(subtask);
                        response = "Subtask added";
                        sendText(exchange, 201, response);
                    } else if (pathArray.length == 3) {
                        manager.updateSubtask(subtask);
                        response = "Subtask updated";
                        sendText(exchange, 201, response);
                    }
                } catch (Exception e) {
                    sendHasInteractions(exchange);
                }
                break;
            case "DELETE":
                manager.removeSubtask(Integer.parseInt(pathArray[2]));
                sendText(exchange, 200, "Delete completed");
                break;
            default:
                exchange.sendResponseHeaders(exchange.getResponseCode(), 0);
        }
    }
}
