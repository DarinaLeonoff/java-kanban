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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class PrioritizedHandlerTest {
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
        baseUri = URI.create("http://localhost:8080/prioritized");
        int[] id = new int[]{0, 2};
        HttpRequest getTask = HttpRequest.newBuilder(baseUri).header("Content-Type", "application/json").GET().build();
        HttpResponse<String>getResp = client.send(getTask, HttpResponse.BodyHandlers.ofString());
        List<? extends Task> prioritized = gson.fromJson(getResp.body(),
                new TypeToken<List<? extends Task>>(){}.getType());
        int[] curId = prioritized.stream()
                .mapToInt(Task::getId)
                .toArray();

        assertArrayEquals(curId, id);
    }
}
