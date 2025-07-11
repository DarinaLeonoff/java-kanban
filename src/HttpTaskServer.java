import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import handlers.*;
import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static HttpServer httpServer;
    private static TaskManager taskManager;

    public HttpTaskServer(TaskManager manager) {
        taskManager = manager;
    }

    public static void main(String[] args) throws IOException {
        taskManager = Managers.getDefault();
        httpServer = new HttpTaskServer(taskManager).start();

        taskManager.createTask(new Task("Test Task", "", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5)));
        taskManager.createEpic(new Epic("Test Epic", "", Status.NEW));
        taskManager.createSubtask(new Subtask("Test Subtask", "", Status.NEW, 1, LocalDateTime.now().plusMinutes(6), Duration.ofMinutes(5)));

        httpServer.createContext("/tasks", new TaskHandler(taskManager));
        httpServer.createContext("/subtasks", new SubtaskHandler(taskManager));
        httpServer.createContext("/epics", new EpicHandler(taskManager));
        httpServer.createContext("/history", new HistoryHandler(taskManager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager));
        httpServer.start();
        System.out.println("HTTP-сервер запущен на порту " + PORT);
    }

    public HttpServer start() {
        try {
            return HttpServer.create(new InetSocketAddress(PORT), 0);
        }catch (IOException e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    public void stop(){
        this.stop();
    }
}
