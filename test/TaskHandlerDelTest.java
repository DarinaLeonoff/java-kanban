import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpServer;
import handlers.DurationAdapter;
import handlers.LocalDateTimeAdapter;
import handlers.OptionalAdapter;
import model.Task;
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

public class TaskHandlerDelTest {

    private static HttpServer server = HttpTaskServer.startServer();
    private URI baseUri;
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new GsonBuilder().registerTypeAdapter(Optional.class, new OptionalAdapter()).registerTypeAdapter(Duration.class, new DurationAdapter()).registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();

    @Test
    void shouldDeleteTaskById() throws Exception {
        baseUri = URI.create("http://localhost:8080/tasks/" + 0);

        // DELETE /tasks/0
        HttpRequest del = HttpRequest.newBuilder(baseUri).DELETE().build();
        HttpResponse<Void> resp = client.send(del, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, resp.statusCode());

        HttpRequest get = HttpRequest.newBuilder(URI.create("http://localhost:8080/tasks/"))   // GET /tasks
                .header("Content-Type", "application/json").GET().build();
        HttpResponse<String> getResp = client.send(get, HttpResponse.BodyHandlers.ofString());
        List<Task> tasks = gson.fromJson(getResp.body(), new TypeToken<List<Task>>() {
        }.getType());
        assertTrue(tasks.isEmpty());
    }
}
