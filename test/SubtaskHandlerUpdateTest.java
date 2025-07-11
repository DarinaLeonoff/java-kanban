import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubtaskHandlerUpdateTest {

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

}
