import adapters.DurationAdapter;
import adapters.LocalDateTimeAdapter;
import adapters.OptionalAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;

import manager.Managers;
import manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class HttpTaskServer {
    private final int PORT = 8080;
    private HttpServer httpServer;
    private TaskManager taskManager;
    private Gson gson;

    public HttpTaskServer(TaskManager manager) throws IOException {
        taskManager = manager;
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).registerTypeAdapter(Duration.class, new DurationAdapter()).registerTypeAdapter(Optional.class, new OptionalAdapter()).create();
    }

    public static void main(String[] args) throws IOException {
        TaskManager manager = Managers.getDefault();

        HttpTaskServer httpServer = new HttpTaskServer(manager);
        HttpContextConfigurator.configure(httpServer);
        httpServer.start();
    }

    public void start() {
        httpServer.start();
        System.out.println("HTTP-сервер запущен на порту " + PORT);
    }

    public void stop() {
        this.stop();
    }

    public HttpServer getServer() {
        return httpServer;
    }

    public Gson getGson() {
        return gson;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

}
