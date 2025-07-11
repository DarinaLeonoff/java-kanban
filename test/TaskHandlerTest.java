import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import handlers.DurationAdapter;
import handlers.LocalDateTimeAdapter;
import handlers.OptionalAdapter;
import model.Status;
import model.Task;
import org.junit.jupiter.api.*;

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

    private static HttpServer server = HttpTaskServer.startServer();
    private URI baseUri;
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new GsonBuilder().registerTypeAdapter(Optional.class, new OptionalAdapter()).registerTypeAdapter(Duration.class, new DurationAdapter()).registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();


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
        HttpRequest post = HttpRequest.newBuilder(baseUri).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(jsonTask)).build();
        HttpResponse<String> postResp = client.send(post, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, postResp.statusCode(), "Создание задачи должно вернуть 201");

        // 2) GET /tasks  → список из одной задачи
        HttpResponse<String> listResp = client.send(HttpRequest.newBuilder(baseUri).GET().build(), HttpResponse.BodyHandlers.ofString());
        assertEquals(200, listResp.statusCode());
        System.out.println(listResp.body());
        List<Task> tasks = gson.fromJson(listResp.body(), new TypeToken<List<Task>>() {
        }.getType());
        assertEquals(2, tasks.size(), "После создания должна быть одна задача");
        assertEquals("JUnit task", tasks.get(1).getTitle());
        server.stop(0);
    }


}
