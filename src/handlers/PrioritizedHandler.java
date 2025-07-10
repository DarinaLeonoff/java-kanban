package handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;

import java.io.IOException;

public class PrioritizedHandler extends BaseHandler{
    private TaskManager manager;
    public PrioritizedHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        super.handle(exchange);
        Gson gson = new GsonBuilder().create();
        String response ="";
        switch (method){
            case "GET":
                response = gson.toJson(manager.getPrioritizedTasks());
                sendText(exchange, 200, response);
                break;
            default:exchange.sendResponseHeaders(exchange.getResponseCode(), 0);
        }
    }
}
