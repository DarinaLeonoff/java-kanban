package handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

public class EpicHandler extends BaseHandler{
    private TaskManager manager;
    public EpicHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        super.handle(exchange);
        Gson gson = new GsonBuilder().registerTypeAdapter(Duration.class, new DurationAdapter()).registerTypeAdapter(Optional.class, new OptionalAdapter()).create();
        String response = "";
        String[] pathArray = super.path.split("/");
        switch (super.method){
            case "GET":
                if(pathArray.length == 2) {
                    response = gson.toJson(manager.getEpics(),new TypeToken<List<Epic>>() {}.getType());
                    sendText(exchange, 200, response);
                } else if(pathArray.length == 3){
                    try {
                        response = gson.toJson(manager.getEpicById(Integer.parseInt(pathArray[2])));
                        sendText(exchange, 200, response);
                    } catch (NumberFormatException e){
                        sendNotFound(exchange);
                    }
                } else if(pathArray.length == 4){
                    try {
                        Epic epic = manager.getEpicById(Integer.parseInt(pathArray[2]));
                    List<Subtask> subtasks =
                            manager.getSubtasks().stream().filter(s -> epic.getSubtasks().contains(s.getId())).toList();
                    response = gson.toJson(subtasks);
                        sendText(exchange, 200, response);
                    } catch (Exception e){
                        sendNotFound(exchange);
                    }
                }

                break;
            case "POST":
                try {
                        Epic epic = gson.fromJson(body, Epic.class);
                        manager.createEpic(epic);
                        response = "Epic added";
                        sendText(exchange, 201, response);
                } catch (Exception e){
                    sendHasInteractions(exchange);
                }
                break;
            case "DELETE":
                manager.removeEpic(Integer.parseInt(pathArray[2]));
                sendText(exchange, 200, "Delete completed");
                break;
            default: exchange.sendResponseHeaders(exchange.getResponseCode(), 0);
        }
    }
}
