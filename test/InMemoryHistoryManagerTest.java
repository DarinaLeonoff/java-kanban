import manager.HistoryManager;
import manager.Managers;
import model.Epic;
import model.Status;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManagerTest {
    private static final HistoryManager manager = Managers.getDefaultHistory();

    @BeforeAll
    public static void addToHistoryTest(){
        for (int i = 0; i < 13; i++) {
            Task task = new Task("t"+i, "d", Status.NEW);
            task.setId(i);
            manager.add(task);
        }
    }
    @Test
    public void checkHistorySize(){
        int size = manager.getHistory().size();
        int maxSize = manager.getMaxHistorySize();
        Assertions.assertEquals(size, maxSize, "History size is " + size);
    }

    @Test
    public void historyContentTest(){
        ArrayList<Task> list = new ArrayList<>();
        for (int i = 3; i < 13; i++) {
            Task task = new Task("t"+i, "d", Status.NEW);
            task.setId(i);
            list.add(task);
        }

        Assertions.assertArrayEquals(list.toArray(), manager.getHistory().toArray(), "Arrays are not the same.");
    }

}

