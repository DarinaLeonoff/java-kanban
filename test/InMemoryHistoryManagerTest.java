import manager.HistoryManager;
import manager.Managers;
import model.Status;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
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
//        int maxSize = manager.getMaxHistorySize();
        Assertions.assertEquals(size, 13, "History size is " + size);
    }

    @Test
    public void historyContentTest(){
        ArrayList<Task> list = new ArrayList<>();
        for (int i = 0; i < 13; i++) {
            Task task = new Task("t"+i, "d", Status.NEW);
            task.setId(i);
            list.add(task);
        }
        Assertions.assertArrayEquals(list.toArray(), manager.getHistory().toArray(), "Arrays are not the same.");
    }

    @Test
    public void timeTest(){ //log time of updating history
        List<Task> list = manager.getHistory();
        long nanoNewTask = System.nanoTime();
        manager.add(list.get(0));
        nanoNewTask = System.nanoTime() - nanoNewTask;
        System.out.println("Time: " + nanoNewTask); //log time
        list = manager.getHistory();
        List<Task> expected = new ArrayList<>();
        for (int i = 1; i < 13; i++) {
            Task task = new Task("t"+i, "d", Status.NEW);
            task.setId(i);
            expected.add(task);
        }
        Task task = new Task("t"+0, "d", Status.NEW);
        task.setId(0);
        expected.add(task);

        Assertions.assertArrayEquals(list.toArray(), expected.toArray(),
                "List are not same: \n" + Arrays.toString(list.toArray()) + "\n" + Arrays.toString(expected.toArray()));
    }

}

