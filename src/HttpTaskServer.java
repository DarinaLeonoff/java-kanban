import com.sun.net.httpserver.HttpServer;
import handlers.*;
import manager.HistoryManager;
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
    private static final TaskManager taskManager = Managers.getDefault();
    private static final HistoryManager historyManager = Managers.getDefaultHistory();


    public static void main(String[] args) throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);

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

    public static HttpServer startServer() {
        try {
            HttpTaskServer.main(null);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return httpServer;
    }

}

//todo Чтобы не дублировать код, можно использовать общий для всех HTTP-обработчиков класс. Для этого создайте
// класс BaseHttpHandler — он будет содержать общие методы для чтения и отправки данных:
//sendText — для отправки общего ответа в случае успеха;
//sendNotFound — для отправки ответа в случае, если объект не был найден;
//sendHasInteractions — для отправки ответа, если при создании или обновлении задача пересекается с уже существующими.
//В этом случае подклассы-обработчики TaskHandler или UserHandler будут наследоваться от BaseHttpHandler, чтобы использовать общие методы. Такой подход уменьшает количество повторяющегося кода и позволяет проще вносить в него изменения.
//Код можно сделать ещё лаконичнее, пробрасывая NotFoundException в TaskManager. Тогда в обработчиках не нужно проверять экземпляр Task на null — можно обрабатывать сразу исключение. Реализуйте такой подход. Также добавьте try — catch — он будет обрабатывать все исключения, которые возникают во время работы программы.



