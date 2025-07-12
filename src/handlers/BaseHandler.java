package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class BaseHandler implements HttpHandler {
    protected String path;
    protected String body;
    protected TaskManager manager;
    protected Gson gson;
    protected String[] pathArray;

    public BaseHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        this.path = exchange.getRequestURI().getPath();
        try (InputStream is = exchange.getRequestBody()) {
            this.body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
        pathArray = path.split("/");
    }


    public void sendText(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] resp = response.getBytes(StandardCharsets.UTF_8);
        send(exchange, resp, statusCode);
    }

    public void sendNotFound(HttpExchange exchange) throws IOException {
        byte[] resp = "Not Found".getBytes(StandardCharsets.UTF_8);
        send(exchange, resp, 404);
    }

    public void sendHasInteractions(HttpExchange exchange) throws IOException {
        byte[] resp = "Has Interactions".getBytes(StandardCharsets.UTF_8);
        send(exchange, resp, 406);
    }

    private void send(HttpExchange exchange, byte[] resp, int code) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
        exchange.sendResponseHeaders(code, resp.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(resp);
        }
    }


}


