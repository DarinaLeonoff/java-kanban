import model.Status;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TaskTest {
    @Test
    public void tasksEqualsIfSameId() {
        Task task = new Task("Title 1", "desc 1", Status.NEW);
        Task task2 = new Task("Tatle 2", "desc 2", Status.DONE);
        task.setId(1);
        task2.setId(1);
        Assertions.assertEquals(task, task2);
    }
}
