package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import model.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class BaseHandler implements HttpHandler {
    protected String method;
    protected String path;
    protected String body;


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        this.method = exchange.getRequestMethod();
        this.path = exchange.getRequestURI().getPath();
        try (InputStream is = exchange.getRequestBody()) {
            this.body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }


    public void sendText(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] resp = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, resp.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(resp);
        }
    }

    public void sendNotFound(HttpExchange exchange) throws IOException {
        byte[] resp = "Not Found".getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
        exchange.sendResponseHeaders(404, resp.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(resp);
        }
    }
    public void sendHasInteractions(HttpExchange exchange) throws IOException {
        byte[] resp = "Has Interactions".getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
        exchange.sendResponseHeaders(406, resp.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(resp);
        }
    }
}
