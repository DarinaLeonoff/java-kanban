import model.Epic;
import model.Status;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

public class EpicTest {
    @Test
    public void epicsEqualsIfSameId() {
        Epic epic1 = new Epic("Tatle 2", "desc 2", Status.DONE);
        Epic epic2 = new Epic("Tatle 2", "desc 2", Status.DONE);
        epic1.setId(1);
        epic2.setId(1);
        Assertions.assertEquals(epic1, epic2, "Epics are not the same");
    }

    @Test
    public void trueIfReturnSameDateTime() {
        Epic epic = new Epic("Tatle 2", "desc 2", Status.DONE);
        LocalDateTime nowTime = LocalDateTime.now();
        epic.setStartFinish(nowTime, nowTime.plusMinutes(30));
        Assertions.assertEquals(epic.getStartTime().get(), nowTime, "Not same date time");
        Assertions.assertEquals(epic.getEndTime().get(), nowTime.plusMinutes(30), "Not same date time");
        Assertions.assertEquals(epic.getDuration(), Duration.ofMinutes(30), "Not same duration");
    }
}
