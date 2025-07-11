import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpServer;
import handlers.DurationAdapter;
import handlers.LocalDateTimeAdapter;
import handlers.OptionalAdapter;
import model.Status;
import model.Subtask;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SubtaskHandlerTest {


    private static HttpServer server;
    private URI baseUri;
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new GsonBuilder().registerTypeAdapter(Optional.class, new OptionalAdapter()).registerTypeAdapter(Duration.class, new DurationAdapter()).registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();

    @BeforeAll
    static void initServer() {
        server = HttpTaskServer.startServer();
    }

    @AfterAll
    static void closeServer() {
        server.stop(0);
    }

    @Test
    void shouldCreateAndReturnTaskList() throws Exception {
        baseUri = URI.create("http://localhost:8080/subtasks");
        // 1) POST /tasks  → 201
        String jsonTask = """
                {
                  "title": "JUnit subtask",
                  "description": "created from test",
                  "status": "NEW",
                  "epicId": 1
                }
                """;
        HttpRequest post = HttpRequest.newBuilder(baseUri).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(jsonTask)).build();
        HttpResponse<String> postResp = client.send(post, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, postResp.statusCode(), "Создание задачи должно вернуть 201");

        // 2) GET /tasks  → список из одной задачи
        HttpResponse<String> listResp = client.send(HttpRequest.newBuilder(baseUri).GET().build(), HttpResponse.BodyHandlers.ofString());

        assertEquals(200, listResp.statusCode());
        List<Subtask> subtasks = gson.fromJson(listResp.body(), new TypeToken<List<Subtask>>() {
        }.getType());
        assertEquals(2, subtasks.size(), "После создания должна быть одна задача");
        assertEquals("JUnit subtask", subtasks.get(1).getTitle());
    }

    @Test
    void shouldUpdateExistingTask() throws Exception {
        baseUri = URI.create("http://localhost:8080/subtasks/" + 2);

        String json = """
                {
                		"epicId": 1,
                        "id": 2,
                        "title": "First post query",
                        "description": "desc",
                        "status": "IN_PROGRESS"
                	}
                """;
        HttpRequest put = HttpRequest.newBuilder(baseUri)   // POST /subtasks/id
                .header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(json)).build();
        HttpResponse<String> resp = client.send(put, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, resp.statusCode());

        HttpRequest get = HttpRequest.newBuilder(baseUri)   // GET /subtasks/id
                .header("Content-Type", "application/json").GET().build();
        HttpResponse<String> getResp = client.send(get, HttpResponse.BodyHandlers.ofString());
        System.out.println(getResp.body());
        Subtask updated = gson.fromJson(getResp.body(), Subtask.class);
        assertEquals(Status.IN_PROGRESS, updated.getStatus());
    }

    @Test
    void shouldDeleteTaskById() throws Exception {
        baseUri = URI.create("http://localhost:8080/subtasks/" + 2);

        // DELETE /subtasks/2
        HttpRequest del = HttpRequest.newBuilder(baseUri).DELETE().build();
        HttpResponse<Void> resp = client.send(del, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, resp.statusCode());

        HttpRequest get = HttpRequest.newBuilder(URI.create("http://localhost:8080/subtasks/"))   // GET /tasks
                .header("Content-Type", "application/json").GET().build();
        HttpResponse<String> getResp = client.send(get, HttpResponse.BodyHandlers.ofString());
        List<Subtask> subtasks = gson.fromJson(getResp.body(), new TypeToken<List<Subtask>>() {
        }.getType());
        assertTrue(subtasks.isEmpty());
    }

}
