import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpServer;
import handlers.DurationAdapter;
import handlers.LocalDateTimeAdapter;
import handlers.OptionalAdapter;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class EpicHandlerTest {

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
    void shouldCreateAndReturnEpicList() throws Exception {
        baseUri = URI.create("http://localhost:8080/epics");
        // 1) GET /epics/id/subtasks
        HttpRequest get = HttpRequest.newBuilder(URI.create("http://localhost:8080/epics/1/subtasks")).header("Content-Type", "application/json").GET().build();
        HttpResponse<String> getResp = client.send(get, HttpResponse.BodyHandlers.ofString());
        List<Subtask> subtasks = gson.fromJson(getResp.body(), new TypeToken<List<Subtask>>() {
        }.getType());
        List<Integer> subId = subtasks.stream().map(Subtask::getId).toList();

        // 2) POST /epics  → 201
        String jsonEpic = """
                {
                  "title": "JUnit epic",
                  "description": "created from test",
                  "status": "NEW"
                }
                """;
        HttpRequest post = HttpRequest.newBuilder(baseUri).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(jsonEpic)).build();
        HttpResponse<String> postResp = client.send(post, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, postResp.statusCode(), "Создание задачи должно вернуть 201");

        // 3) GET /tasks  → список из 2 задач
        HttpResponse<String> listResp = client.send(HttpRequest.newBuilder(baseUri).GET().build(), HttpResponse.BodyHandlers.ofString());

        assertEquals(200, listResp.statusCode());
        List<Epic> epics = gson.fromJson(listResp.body(), new TypeToken<List<Epic>>() {
        }.getType());
        assertEquals(2, epics.size(), "После создания должна быть 2 задачи");
        assertEquals("JUnit epic", epics.get(1).getTitle());
        assertArrayEquals(epics.get(0).getSubtasks().toArray(), subId.toArray());
    }

    @Test
    void shouldDeleteTaskById() throws Exception {
        baseUri = URI.create("http://localhost:8080/epics/" + 1);

        // DELETE /tasks/0
        HttpRequest del = HttpRequest.newBuilder(baseUri).DELETE().build();
        HttpResponse<Void> resp = client.send(del, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, resp.statusCode());

        HttpRequest get = HttpRequest.newBuilder(URI.create("http://localhost:8080/epics"))   // GET /epics
                .header("Content-Type", "application/json").GET().build();
        HttpResponse<String> getResp = client.send(get, HttpResponse.BodyHandlers.ofString());
        List<Epic> epics = gson.fromJson(getResp.body(), new TypeToken<List<Epic>>() {
        }.getType());
        assertTrue(epics.isEmpty());

        HttpRequest getSubtask = HttpRequest.newBuilder(URI.create("http://localhost:8080/subtasks"))   // GET /epics
                .header("Content-Type", "application/json").GET().build();
        HttpResponse<String> getRespSub = client.send(getSubtask, HttpResponse.BodyHandlers.ofString());
        List<Subtask> subtasks = gson.fromJson(getResp.body(), new TypeToken<List<Subtask>>() {
        }.getType());
        assertTrue(subtasks.isEmpty());
    }
}
