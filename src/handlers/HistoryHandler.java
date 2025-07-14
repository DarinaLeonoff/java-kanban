package handlers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import model.Task;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHandler {

    public HistoryHandler(TaskManager manager, Gson gson) {
        super(manager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        super.handle(exchange);
        if (exchange.getRequestMethod().equals("GET")) {
            handleGet(exchange);
        } else {
            exchange.sendResponseHeaders(exchange.getResponseCode(), 0);
        }
    }

    private void handleGet(HttpExchange ex)throws IOException {
        sendText(ex, 200,
                gson.toJson(manager.getHistory(), new TypeToken<List<? extends Task>>() {
                }.getType()));
    }
}
