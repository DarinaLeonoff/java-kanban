package handlers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import model.Epic;
import model.Subtask;

import java.io.IOException;
import java.util.List;

public class EpicHandler extends BaseHandler {
    public EpicHandler(TaskManager manager, Gson gson) {
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
            sendText(ex, 200, gson.toJson(manager.getEpics(), new TypeToken<List<Epic>>() {
            }.getType()));
        } else if (pathArray.length == 3) {
            try {
                sendText(ex, 200, gson.toJson(manager.getEpicById(Integer.parseInt(pathArray[2]))));
            } catch (NumberFormatException e) {
                sendNotFound(ex);
            }
        } else if (pathArray.length == 4) {
            try {
                Epic epic = manager.getEpicById(Integer.parseInt(pathArray[2]));
                List<Subtask> subtasks = manager.getSubtasks().stream().filter(s -> epic.getSubtasks().contains(s.getId())).toList();
                sendText(ex, 200, gson.toJson(subtasks, new TypeToken<List<Subtask>>() {
                }.getType()));
            } catch (Exception e) {
                sendNotFound(ex);
            }
        }

    }

    private void handlePost(HttpExchange ex) throws IOException {
        try {
            Epic epic = gson.fromJson(body, Epic.class);
            manager.createEpic(epic);
            sendText(ex, 201, "Epic added");
        } catch (Exception e) {
            sendHasInteractions(ex);
        }
    }

    private void handleDelete(HttpExchange ex) throws IOException {
        try {
            manager.removeEpic(Integer.parseInt(pathArray[2]));
            sendText(ex, 200, "Delete completed");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

