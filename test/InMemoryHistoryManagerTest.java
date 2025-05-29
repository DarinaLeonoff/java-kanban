import historyManager.HistoryManager;
import historyManager.InMemoryHistoryManager;
import model.Status;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class InMemoryHistoryManagerTest {
    private static HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    public void testAddAndGetHistory() {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW);
        task1.setId(1);

        Task task2 = new Task("Task 2", "Description 2", Status.NEW);
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();

        Assertions.assertEquals(2, history.size());
        Assertions.assertEquals(task1, history.get(0));
        Assertions.assertEquals(task2, history.get(1));
    }

    @Test
    public void testAddDuplicateMovesToEnd() {
        Task task1 = new Task("Task 1", "Description", Status.NEW);
        task1.setId(1);

        Task task2 = new Task("Task 2", "Description", Status.NEW);
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task1); // повторное добавление

        List<Task> history = historyManager.getHistory();

        Assertions.assertEquals(2, history.size());
        Assertions.assertEquals(task2, history.get(0));
        Assertions.assertEquals(task1, history.get(1));
    }

    @Test
    void testRemoveFromHistory() {
        Task task1 = new Task("Task 1", "Description", Status.NEW);
        task1.setId(1);

        Task task2 = new Task("Task 2", "Description", Status.NEW);
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(task1);

        List<Task> history = historyManager.getHistory();
        Assertions.assertEquals(1, history.size());
        Assertions.assertEquals(task2, history.get(0));
    }

    @Test
    void testEmptyHistory() {
        List<Task> history = historyManager.getHistory();
        Assertions.assertTrue(history.isEmpty(), "History should be empty initially");
    }

    @Test
    void testAddNullTask() {
        historyManager.add(null);
        List<Task> history = historyManager.getHistory();
        Assertions.assertTrue(history.isEmpty(), "Null task should not be added");
    }

}

