import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpServer;
import handlers.DurationAdapter;
import handlers.LocalDateTimeAdapter;
import handlers.OptionalAdapter;
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

public class SubtaskHandlerDelTest {
    private HttpServer server = HttpTaskServer.startServer();;
    private URI baseUri;
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new GsonBuilder().registerTypeAdapter(Optional.class, new OptionalAdapter()).registerTypeAdapter(Duration.class, new DurationAdapter()).registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();

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
        server.stop(0);
    }
}
