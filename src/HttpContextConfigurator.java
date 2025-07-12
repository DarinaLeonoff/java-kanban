import com.sun.net.httpserver.HttpServer;
import handlers.*;
import manager.TaskManager;

public class HttpContextConfigurator {
    public static void configure(HttpServer server, TaskManager taskManager) {
        server.createContext("/tasks", new TaskHandler(taskManager));
        server.createContext("/subtasks", new SubtaskHandler(taskManager));
        server.createContext("/epics", new EpicHandler(taskManager));
        server.createContext("/history", new HistoryHandler(taskManager));
        server.createContext("/prioritized", new PrioritizedHandler(taskManager));
    }
}
