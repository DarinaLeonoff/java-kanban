import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import handlers.*;
import manager.TaskManager;

public class HttpContextConfigurator {
    public static void configure(HttpTaskServer taskServer) {
        HttpServer server = taskServer.getServer();
        TaskManager taskManager = taskServer.getTaskManager();
        Gson gson = taskServer.getGson();
        server.createContext("/tasks", new TaskHandler(taskManager, gson));
        server.createContext("/subtasks", new SubtaskHandler(taskManager, gson));
        server.createContext("/epics", new EpicHandler(taskManager, gson));
        server.createContext("/history", new HistoryHandler(taskManager, gson));
        server.createContext("/prioritized", new PrioritizedHandler(taskManager, gson));
    }
}
