import model.Status;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SubtaskTest {
    @Test
    public void subtasksEqualsIfSameId(){
        model.Subtask subtask1 = new model.Subtask("Tatle 2", "desc 2", Status.DONE, 0);
        model.Subtask subtask2 = new model.Subtask("Tatle 2", "desc 2", Status.DONE, 0);
        subtask1.setId(1);
        subtask2.setId(1);
        Assertions.assertEquals(subtask1, subtask2, "Subtasks are not the same");
    }
}
