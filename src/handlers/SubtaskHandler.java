package handlers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import model.Subtask;

import java.io.IOException;
import java.util.List;

public class SubtaskHandler extends BaseHandler {

    public SubtaskHandler(TaskManager manager, Gson gson) {
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
            sendText(ex, 200, gson.toJson(manager.getSubtasks(), new TypeToken<List<Subtask>>() {
            }.getType()));
        } else if (pathArray.length == 3) {
            try {
                sendText(ex, 200, gson.toJson(manager.getSubtaskById(Integer.parseInt(pathArray[2]))));
            } catch (NumberFormatException e) {
                sendNotFound(ex);
            }
        }
    }

    private void handlePost(HttpExchange ex) throws IOException {
        try {
            Subtask subtask = gson.fromJson(body, Subtask.class);
            if (pathArray.length == 2) {
                manager.createSubtask(subtask);
                System.out.println("added");
                sendText(ex, 201, "Subtask added");
            } else if (pathArray.length == 3) {
                manager.updateSubtask(subtask);
                sendText(ex, 201, "Subtask updated");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendHasInteractions(ex);
        }
    }

    private void handleDelete(HttpExchange ex) throws IOException {
        manager.removeSubtask(Integer.parseInt(pathArray[2]));
        sendText(ex, 200, "Delete completed");
    }

}
