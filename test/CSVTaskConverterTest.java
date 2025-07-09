import manager.FileBackedTaskManager;
import manager.Managers;
import manager.TaskManager;
import model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class CSVTaskConverterTest {


    @Test
    public void StringConvertToTaskCheck() {

        String taskStr = "0,TASK,T,NEW,D,-1,2025-06-29T21:50:18,20,\n";
        String epicStr = "1,EPIC,T1,NEW,D1,-1,2025-06-29T23:50:18,20,\n";
        String subtaskStr = "2,SUBTASK,T2,NEW,D2,1,2025-06-29T23:50:18,20,\n";

        Task task = CSVTaskConverter.fromString(taskStr);
        Epic epic = (Epic) CSVTaskConverter.fromString(epicStr);
        Subtask subtask = (Subtask) CSVTaskConverter.fromString(subtaskStr);

        String expTask = CSVTaskConverter.taskToCSV(task);
        String expEpic = CSVTaskConverter.taskToCSV(epic);
        String expSubtask = CSVTaskConverter.taskToCSV(subtask);

        Assertions.assertEquals(expTask, taskStr, "Not same tasks \n" + expTask + "\n" + taskStr);
        Assertions.assertEquals(expEpic, epicStr, "Not same epic\n" + expEpic + "\n" + epicStr);
        Assertions.assertEquals(expSubtask, subtaskStr, "Not same subtasks\n" + expSubtask + "\n" + subtaskStr);
    }
}
