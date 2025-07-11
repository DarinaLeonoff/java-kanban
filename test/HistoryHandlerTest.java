import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpServer;
import handlers.DurationAdapter;
import handlers.LocalDateTimeAdapter;
import handlers.OptionalAdapter;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HistoryHandlerTest {
    private static HttpServer server;
    private URI baseUri;
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Optional.class, new OptionalAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

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
        baseUri = URI.create("http://localhost:8080/history");
        URI taskUri = URI.create("http://localhost:8080/tasks/0");

        HttpRequest getTask = HttpRequest.newBuilder(taskUri).header("Content-Type", "application/json").GET().build();
        HttpResponse<String> postResp = client.send(getTask, HttpResponse.BodyHandlers.ofString());
        int id = gson.fromJson(postResp.body(), Task.class).getId();


        HttpResponse<String> listResp = client.send(HttpRequest.newBuilder(baseUri).GET().build(), HttpResponse.BodyHandlers.ofString());
        assertEquals(200, listResp.statusCode());
        List<Task> history = gson.fromJson(listResp.body(), new TypeToken<List<Task>>() {
        }.getType());
        assertEquals(1, history.size(), "После создания должна быть одна задача");
        assertEquals(id, history.get(0).getId());
    }
}
