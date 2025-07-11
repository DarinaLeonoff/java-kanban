import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import handlers.DurationAdapter;
import handlers.LocalDateTimeAdapter;
import handlers.OptionalAdapter;
import handlers.TaskHandler;
import manager.InMemoryTaskManager;
import manager.Managers;
import manager.TaskManager;
import model.Status;
import model.Task;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.sun.net.httpserver.HttpServer;

import static org.junit.jupiter.api.Assertions.*;

class TaskHandlerTest {

    private static HttpServer server;
    private URI baseUri;
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Optional.class, new OptionalAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    @BeforeAll
    static void initServer(){
        server = HttpTaskServer.startServer();
    }
    @AfterAll
    static void closeServer(){
        server.stop(0);
    }

    @Test
    void shouldCreateAndReturnTaskList() throws Exception {
        baseUri = URI.create("http://localhost:8080/tasks");
        // 1) POST /tasks  → 201
        String jsonTask = """
            {
              "title": "JUnit task",
              "description": "created from test",
              "status": "NEW"
            }
            """;
        HttpRequest post = HttpRequest.newBuilder(baseUri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .build();
        HttpResponse<String> postResp = client.send(post, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, postResp.statusCode(), "Создание задачи должно вернуть 201");

        // 2) GET /tasks  → список из одной задачи
        HttpResponse<String> listResp = client.send(
                HttpRequest.newBuilder(baseUri).GET().build(),
                HttpResponse.BodyHandlers.ofString());

        assertEquals(200, listResp.statusCode());
        List<Task> tasks = gson.fromJson(listResp.body(), new TypeToken<List<Task>>() {}.getType());
        assertEquals(2, tasks.size(), "После создания должна быть одна задача");
        assertEquals("JUnit task", tasks.get(1).getTitle());
    }

    @Test
    void shouldUpdateExistingTask() throws Exception {
        baseUri = URI.create("http://localhost:8080/tasks/"+0);

        String json = """
                {
                		"id": 0,
                		"title": "1 ",
                		"description": "11111",
                		"status": "IN_PROGRESS",
                		"duration": 0
                	}
                """;
        System.out.println("json: " + json);
        HttpRequest put = HttpRequest.newBuilder(baseUri)   // POST /tasks/id
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> resp = client.send(put, HttpResponse.BodyHandlers.ofString());
        System.out.println("body: " + resp.body());
        assertEquals(201, resp.statusCode());

        HttpRequest get = HttpRequest.newBuilder(baseUri)   // GET /tasks/id
                .header("Content-Type", "application/json")
                .GET()
                .build();
        HttpResponse<String> getResp = client.send(get, HttpResponse.BodyHandlers.ofString());
        System.out.println(getResp.body());
        Task updated = gson.fromJson(getResp.body(), Task.class);
        assertEquals(Status.IN_PROGRESS, updated.getStatus());
    }

    @Test
    void shouldDeleteTaskById() throws Exception {
        baseUri = URI.create("http://localhost:8080/tasks/"+0);

        // DELETE /tasks/0
        HttpRequest del = HttpRequest.newBuilder(baseUri)
                .DELETE()
                .build();
        HttpResponse<Void> resp = client.send(del, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, resp.statusCode());

        HttpRequest get = HttpRequest.newBuilder(URI.create("http://localhost:8080/tasks/"))   // GET /tasks
                .header("Content-Type", "application/json")
                .GET()
                .build();
        HttpResponse<String> getResp = client.send(get, HttpResponse.BodyHandlers.ofString());
        List<Task> tasks = gson.fromJson(getResp.body(), new TypeToken<List<Task>>() {}.getType());
        assertTrue(tasks.isEmpty());
    }
}
