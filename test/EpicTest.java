import model.Epic;
import model.Status;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EpicTest {
    @Test
    public void epicsEqualsIfSameId() {
        Epic epic1 = new Epic("Tatle 2", "desc 2", Status.DONE);
        Epic epic2 = new Epic("Tatle 2", "desc 2", Status.DONE);
        epic1.setId(1);
        epic2.setId(1);
        Assertions.assertEquals(epic1, epic2, "Epics are not the same");
    }
}
