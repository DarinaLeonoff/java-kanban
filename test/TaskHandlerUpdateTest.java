import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import handlers.DurationAdapter;
import handlers.LocalDateTimeAdapter;
import handlers.OptionalAdapter;
import model.Status;
import model.Task;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskHandlerUpdateTest {

    private static HttpServer server = HttpTaskServer.startServer();
    private URI baseUri;
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new GsonBuilder().registerTypeAdapter(Optional.class, new OptionalAdapter()).registerTypeAdapter(Duration.class, new DurationAdapter()).registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();

    @Test
    void shouldUpdateExistingTask() throws Exception {
        baseUri = URI.create("http://localhost:8080/tasks/" + 0);

        String json = """
                {
                		"id": 0,
                		"title": "1 ",
                		"description": "11111",
                		"status": "IN_PROGRESS",
                		"duration": 0
                	}
                """;
        HttpRequest put = HttpRequest.newBuilder(baseUri)   // POST /tasks/id
                .header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(json)).build();
        HttpResponse<String> resp = client.send(put, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, resp.statusCode());

        HttpRequest get = HttpRequest.newBuilder(baseUri)   // GET /tasks/id
                .header("Content-Type", "application/json").GET().build();
        HttpResponse<String> getResp = client.send(get, HttpResponse.BodyHandlers.ofString());
        Task updated = gson.fromJson(getResp.body(), Task.class);
        assertEquals(Status.IN_PROGRESS, updated.getStatus());
    }
}
