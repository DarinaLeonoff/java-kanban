import com.sun.net.httpserver.HttpServer;

import manager.Managers;
import manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private final int PORT = 8080;
    private HttpServer httpServer;
    private TaskManager taskManager;

    public HttpTaskServer(TaskManager manager) throws IOException {
        taskManager = manager;
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
    }

    public static void main(String[] args) throws IOException {
        TaskManager manager = Managers.getDefault();

        HttpTaskServer httpServer = new HttpTaskServer(manager);
        HttpContextConfigurator.configure(httpServer.getServer(), manager);
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

}
