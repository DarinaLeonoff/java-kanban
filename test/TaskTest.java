import model.Status;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

public class TaskTest {
    @Test
    public void tasksEqualsIfSameId() {
        Task task = new Task("Title 1", "desc 1", Status.NEW);
        Task task2 = new Task("Tatle 2", "desc 2", Status.DONE);
        task.setId(1);
        task2.setId(1);
        Assertions.assertEquals(task, task2);
    }

    @Test
    public void trueIfTaskReturnSameDateTime() {
        LocalDateTime nowTime = LocalDateTime.now();
        Task task = new Task("Title 1", "desc 1", Status.NEW, nowTime, Duration.ofMinutes(20));
        task.setId(1);
        Assertions.assertEquals(task.getStartTime().get(), nowTime, "not same date time");
        Assertions.assertEquals(task.getDuration(), Duration.ofMinutes(20), "not same duration");
        Assertions.assertEquals(task.getEndTime().get(), nowTime.plusMinutes(20), "not same date time");
    }
}
