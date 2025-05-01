import manager.InMemoryTaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class InMemoryTaskManagerTest {
    private static InMemoryTaskManager manager = new InMemoryTaskManager();
    @BeforeAll
    public static void init(){
        for (int i = 0; i < 15; i++) {
            manager.createEpic(new Epic( "title1", "desc1", Status.NEW));//0-14
        }

//        manager.createEpic(new Epic("title2", "desc2", Status.NEW));//1
//        manager.createSubtask(new Subtask( "subtitle2.1", "subdesk", Status.NEW, 1));//2
//        manager.createSubtask(new Subtask("subtitle2.2", "subdesk", Status.NEW, 1));//3
//        manager.createEpic(new Epic("title3", "desc3", Status.NEW));//4
//        manager.createSubtask(new Subtask("subtitle3.1", "subdesk", Status.NEW, 4));//5
    }

    @Test
    public void checkHistoryIsEmpty(){
        Assertions.assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    public void checkHistory(){
        for (int i = 0; i < 10; i++) {
            manager.getEpicById(i);
        }
        int[] list = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        int[] history = new int[10];
        List<Integer> historyList = manager.getHistory();
        for (int i = 0; i < 10; i++) {
            history[i] = historyList.get(i);
        }
        Assertions.assertArrayEquals(history, list, "Uncorrect historu return");
    }
}
