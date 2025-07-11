package handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import manager.HistoryManager;
import manager.TaskManager;
import model.Subtask;
import model.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class HistoryHandler extends BaseHandler {
    private TaskManager manager;

    public HistoryHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        super.handle(exchange);
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(Optional.class, new OptionalAdapter())
                .create();
        String response = "";
        if (method.equals("GET")) {
            response = gson.toJson(manager.getHistory(), new TypeToken<List<? extends Task>>() {
            }.getType());
            sendText(exchange, 200, response);
        } else {
            exchange.sendResponseHeaders(exchange.getResponseCode(), 0);
        }
    }
}
